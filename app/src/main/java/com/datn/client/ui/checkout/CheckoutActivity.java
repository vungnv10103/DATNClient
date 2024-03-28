package com.datn.client.ui.checkout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.activity.EBankingActivity;
import com.datn.client.adapter.PaymentMethodAdapter;
import com.datn.client.adapter.ProductCheckoutAdapter;
import com.datn.client.databinding.ActivityCheckoutBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models.PaymentMethod;
import com.datn.client.models.ProductCart;
import com.datn.client.models._BaseModel;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.services.zalo.CreateOrder;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.ui.product.DetailProductActivity.TYPE_BUY;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.MyFormat;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;


public class CheckoutActivity extends AppCompatActivity implements ICheckoutView {
    private static final String TAG = CheckoutActivity.class.getSimpleName();
    private ActivityCheckoutBinding binding;
    private CheckoutPresenter checkoutPresenter;
    private PreferenceManager preferenceManager;

    private LinearProgressIndicator progressLoading;
    private CircularProgressIndicator progressBarProduct;
    private RecyclerView rcvProduct, rcvPaymentMethod;
    private LinearLayout layoutDelivery, layoutEBanking, layoutZaloPay;

    private Button btnEBanking, btnZaloPay, btnDelivery2, btnZaloPay2, btnDelivery3, btnEBanking3;
    private boolean isDelivery = true, isEBanking, isZaloPay;

    private Customer mCustomer;
    private String mToken;

    private HashMap<Integer, String> mPaymentMethod;
    private List<PaymentMethod> paymentMethodList;
    private List<ProductCart> mProductCart;
    private int mTypeBuy = TYPE_BUY.ADD_TO_CART.getValue();

    public boolean isLoading = false;


    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            finish();
        }
    };

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri
                MyDialog.gI().startDlgOK(CheckoutActivity.this, uri.toString());
            });

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    MyDialog.gI().startDlgOK(CheckoutActivity.this, result.getResultCode() + "");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();
        initEventClick();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().checkCustomer(this);
        mToken = ManagerUser.gI().checkToken(this);
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
        initService();
        initZaloPay();

        binding.layoutAddress.setOnLongClickListener(v -> {
            mGetContent.launch("image/*");
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        setLoading(true);
        ArrayList<ProductCart> dataProductCarts = getIntent().getParcelableArrayListExtra("productCart");
        if (dataProductCarts != null) {
            this.mProductCart = dataProductCarts;
            mTypeBuy = TYPE_BUY.BUY_NOW.getValue();
            displayProductCart();
            displayPrice();
        } else {
            checkoutPresenter.getProductCheckout();
        }
//        checkoutPresenter.getPaymentMethod();
    }

    private void displayPaymentMethod() {
        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, mPaymentMethod,
                paymentMethodList, new IAction() {
            @Override
            public void onClick(_BaseModel paymentMethod) {
                showToast(paymentMethod.toString());
            }

            @Override
            public void onLongClick(_BaseModel paymentMethod) {

            }

            @Override
            public void onItemClick(_BaseModel paymentMethod) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvPaymentMethod.setLayoutManager(linearLayoutManager);
        rcvPaymentMethod.setAdapter(paymentMethodAdapter);
        rcvPaymentMethod.setVisibility(View.VISIBLE);
    }

    private void displayPrice() {
        int count = 0;
        int price = 0;
        for (ProductCart productCart : mProductCart) {
            int quantityCart = Integer.parseInt(productCart.getQuantity_cart());
            count += quantityCart;
            price += Integer.parseInt(productCart.getPrice()) * quantityCart;
        }
        binding.tvQuantity.setText(String.valueOf(count));
        binding.tvTotalPrice.setText(MyFormat.formatCurrency(String.valueOf(price)));
        binding.tvPricePayment.setText(MyFormat.formatCurrency(String.valueOf(price)));
    }

    private void displayProductCart() {
        if (mProductCart.isEmpty()) {
            finish();
        }
        ProductCheckoutAdapter productCheckoutAdapter = new ProductCheckoutAdapter(this, mProductCart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvProduct.setLayoutManager(linearLayoutManager);
        rcvProduct.setAdapter(productCheckoutAdapter);
        progressBarProduct.setVisibility(View.GONE);
        rcvProduct.setVisibility(View.VISIBLE);
        setLoading(false);
    }

    @Override
    public void onListProduct(List<ProductCart> productCartList) {
        this.mProductCart = productCartList;
        displayProductCart();
        displayPrice();
    }

    @Override
    public void onListPaymentMethod(@NonNull HashMap<Integer, String> paymentMethod) {
        this.mPaymentMethod = paymentMethod;
        paymentMethodList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : paymentMethod.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            paymentMethodList.add(new PaymentMethod(key, value));
        }
        displayPaymentMethod();
    }

    @Override
    public void onCreateOrder(String amount) {
        createOrderZaloPay(amount);
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {

    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(this, overlayMessages, checkoutPresenter);
    }

    @Override
    public void onThrowMessage(MessageResponse message) {
        if (message != null) {
            switch (message.getStatusCode()) {
                case 200:
                    MyDialog.gI().startDlgOKWithAction(this, message.getTitle(), message.getContent(), (dialog, which) -> finish());
                    break;
                case 400:
                default:
                    MyDialog.gI().startDlgOK(this, message.getContent());
                    break;
            }
        }
    }

    @Override
    public void onThrowLog(String key, String message) {
        Log.w(key, "onThrowLog: " + message);
    }

    @Override
    public void onFinish() {
        reLogin();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        checkoutPresenter = new CheckoutPresenter(CheckoutActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private void initZaloPay() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
    }

    private void createOrderZaloPay(String amount) {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(amount);
            String code = data.getString("return_code");
            switch (code) {
                case "1":
                    String token = data.getString("zp_trans_token");
                    Log.w(TAG, "createOrderZaloPay: Successful: " + token);
                    payOrderZaloPay(token);
                    break;
                case "2":
                    Log.w(TAG, "createOrderZaloPay: Error: FAIL");
                    break;
                case "3":
                    Log.w(TAG, "createOrderZaloPay: Pending: PROCESSING");
                    break;
            }
        } catch (Exception e) {
            Log.w(TAG, "createOrderZaloPay: " + e.getMessage());
            MyDialog.gI().startDlgOK(CheckoutActivity.this, e.getMessage());
        }
    }

    private void payOrderZaloPay(String token) {
        ZaloPaySDK.getInstance().payOrder(CheckoutActivity.this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Payment Success: " + String.format("TransactionId: %s - TransToken: %s", transactionId, transToken));
                    if (mTypeBuy == TYPE_BUY.ADD_TO_CART.getValue()) {
                        checkoutPresenter.createOrderZaloPay();
                    } else if (mTypeBuy == TYPE_BUY.BUY_NOW.getValue()) {
                        checkoutPresenter.createOrderZaloPayNow(mProductCart);
                    }
                });

            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                runOnUiThread(() -> {
                    setLoading(false);
                    MyDialog.gI().startDlgOK(CheckoutActivity.this, "User Cancel Payment",
                            String.format("zpTransToken: %s \n", zpTransToken));
                });
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                runOnUiThread(() -> {
                    setLoading(false);
                    MyDialog.gI().startDlgOK(CheckoutActivity.this, "Payment Fail",
                            String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken));
                });
            }
        });
    }

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        progressLoading.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    private void initEventClick() {
        eventPaymentMethod();
        binding.btnPayment.setOnClickListener(v -> {
            if (!isLoading) {
                setLoading(true);
                if (isZaloPay) {
                    if (mTypeBuy == TYPE_BUY.ADD_TO_CART.getValue()) {
                        checkoutPresenter.getAmountZaloPay();
                    } else if (mTypeBuy == TYPE_BUY.BUY_NOW.getValue()) {
                        checkoutPresenter.getAmountZaloPay(mProductCart);
                    }
                } else if (isEBanking) {
                    Intent intent = new Intent(CheckoutActivity.this, EBankingActivity.class);
                    if (mTypeBuy == TYPE_BUY.BUY_NOW.getValue()) {
                        intent.putExtra("productID", mProductCart.get(0).getProduct_id());
                        intent.putExtra("quantity", mProductCart.get(0).getQuantity_cart());
                    }
                    activityLauncher.launch(intent);
                } else if (isDelivery) {
                    if (mTypeBuy == TYPE_BUY.ADD_TO_CART.getValue()) {
                        checkoutPresenter.createOrderDelivery();
                    } else if (mTypeBuy == TYPE_BUY.BUY_NOW.getValue()) {
                        MyDialog.gI().startDlgOKWithAction(this, "Updating...", (dialog, which) -> setLoading(false));
                    }
                }
            }
        });
    }


    private void eventPaymentMethod() {
        btnEBanking.setOnClickListener(v -> {
            if (!isLoading) {
                displayEBankingLayout();
            }
        });
        btnDelivery2.setOnClickListener(v -> {
            if (!isLoading) {
                displayDeliveryLayout();
            }
        });
        btnZaloPay2.setOnClickListener(v -> {
            if (!isLoading) {
                displayZaloPayLayout();
            }
        });
        btnZaloPay.setOnClickListener(v -> {
            if (!isLoading) {
                displayZaloPayLayout();
            }
        });
        btnDelivery3.setOnClickListener(v -> {
            if (!isLoading) {
                displayDeliveryLayout();
            }
        });
        btnEBanking3.setOnClickListener(v -> {
            if (!isLoading) {
                displayEBankingLayout();
            }
        });
    }

    private void displayDeliveryLayout() {
        isDelivery = true;
        isEBanking = isZaloPay = false;
        layoutDelivery.setVisibility(View.VISIBLE);
        layoutEBanking.setVisibility(View.GONE);
        layoutZaloPay.setVisibility(View.GONE);
    }

    private void displayEBankingLayout() {
        isEBanking = true;
        isDelivery = isZaloPay = false;
        layoutEBanking.setVisibility(View.VISIBLE);
        layoutDelivery.setVisibility(View.GONE);
        layoutZaloPay.setVisibility(View.GONE);
    }

    private void displayZaloPayLayout() {
        isZaloPay = true;
        isDelivery = isEBanking = false;
        layoutZaloPay.setVisibility(View.VISIBLE);
        layoutDelivery.setVisibility(View.GONE);
        layoutEBanking.setVisibility(View.GONE);
    }


    private void initUI() {
        progressLoading = binding.progressLoading;
        progressBarProduct = binding.progressbarProduct;
        rcvProduct = binding.rcvProduct;
        rcvPaymentMethod = binding.rcvOptionsPayments;

        layoutDelivery = binding.layoutDelivery;
        layoutEBanking = binding.layoutEBanking;
        layoutZaloPay = binding.layoutZalopay;

        btnEBanking = binding.btnEBanking;
        btnZaloPay = binding.btnZalopay;
        btnDelivery2 = binding.btnDelivery2;
        btnZaloPay2 = binding.btnZalopay2;
        btnDelivery3 = binding.btnDelivery3;
        btnEBanking3 = binding.btnEBanking3;
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data != null) {
                String resultValue = data.getStringExtra("title");
                if (resultValue != null) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        }
    }

    @Override

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkoutPresenter.onCancelAPI();
    }
}