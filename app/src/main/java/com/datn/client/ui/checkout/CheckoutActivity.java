package com.datn.client.ui.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.adapter.PaymentMethodAdapter;
import com.datn.client.adapter.ProductCheckoutAdapter;
import com.datn.client.databinding.ActivityCheckoutBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.PaymentMethod;
import com.datn.client.models.ProductCart;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.services.zalo.CreateOrder;
import com.datn.client.ui.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

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

    private ProgressBar progressBarProduct;
    private RecyclerView rcvProduct, rcvPaymentMethod;
    private LinearLayout layoutDelivery, layoutEBanking, layoutZaloPay;

    private Button btnEBanking, btnZaloPay, btnDelivery2, btnZaloPay2, btnDelivery3, btnEBanking3;
    private boolean isDelivery = true, isEBanking, isZaloPay;

    private Customer mCustomer;
    private String mToken;

    private HashMap<Integer, String> mPaymentMethod;
    private List<PaymentMethod> paymentMethodList;
    private List<ProductCart> mProductCart;


    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();
        initEventClick();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
        checkLogin();
        initService();
        initZaloPay();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkoutPresenter.getProductCheckout();
//        checkoutPresenter.getPaymentMethod();
    }

    private void displayPaymentMethod() {
        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, mPaymentMethod, paymentMethodList, baseModel -> showToast(baseModel.toString()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvPaymentMethod.setLayoutManager(linearLayoutManager);
        rcvPaymentMethod.setAdapter(paymentMethodAdapter);
    }

    private void displayProductCart() {
        ProductCheckoutAdapter productCheckoutAdapter = new ProductCheckoutAdapter(this, mProductCart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvProduct.setLayoutManager(linearLayoutManager);
        rcvProduct.setAdapter(productCheckoutAdapter);
        progressBarProduct.setVisibility(View.GONE);
        rcvProduct.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListProduct(List<ProductCart> productCartList) {
        this.mProductCart = productCartList;
        displayProductCart();
    }

    @Override
    public void onListPaymentMethod(HashMap<Integer, String> paymentMethod) {
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
    public void onThrowMessage(String message) {
        MyDialog.gI().startDlgOK(this, message);
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            finishAffinity();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            finishAffinity();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        checkoutPresenter = new CheckoutPresenter(this, apiService, mToken, mCustomer.get_id());
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
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.w(TAG, "createOrderZaloPay: " + token);
                payOrderZaloPay(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void payOrderZaloPay(String token) {
        ZaloPaySDK.getInstance().payOrder(CheckoutActivity.this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                runOnUiThread(() -> MyDialog.gI().startDlgOK(CheckoutActivity.this, "Payment Success",
                        String.format("TransactionId: %s - TransToken: %s", transactionId, transToken)));
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                runOnUiThread(() -> MyDialog.gI().startDlgOK(CheckoutActivity.this, "User Cancel Payment",
                        String.format("zpTransToken: %s \n", zpTransToken)));
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                runOnUiThread(() -> MyDialog.gI().startDlgOK(CheckoutActivity.this, "Payment Fail",
                        String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken)));
            }
        });
    }

    private void initEventClick() {
        eventPaymentMethod();

    }

    private void eventPaymentMethod() {
        btnEBanking.setOnClickListener(v -> displayEBankingLayout());
        btnDelivery2.setOnClickListener(v -> displayDeliveryLayout());
        btnZaloPay2.setOnClickListener(v -> displayZaloPayLayout());
        btnZaloPay.setOnClickListener(v -> displayZaloPayLayout());
        btnDelivery3.setOnClickListener(v -> displayDeliveryLayout());
        btnEBanking3.setOnClickListener(v -> displayEBankingLayout());
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}