package com.datn.client.ui.checkout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.datn.client.action.IAction;
import com.datn.client.adapter.PaymentMethodAdapter;
import com.datn.client.adapter.ProductCheckoutAdapter;
import com.datn.client.databinding.ActivityCheckoutBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.PaymentMethod;
import com.datn.client.models.ProductCart;
import com.datn.client.models._BaseModel;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity implements ICheckoutView {
    private ActivityCheckoutBinding binding;

    private ApiService apiService;
    private CheckoutPresenter checkoutPresenter;
    private PreferenceManager preferenceManager;

    private RecyclerView rcvProduct, rcvPaymentMethod;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkoutPresenter.getProductCheckout();
        checkoutPresenter.getPaymentMethod();
    }

    private void displayPaymentMethod() {
        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, mPaymentMethod, paymentMethodList, new IAction() {
            @Override
            public void onClick(_BaseModel baseModel) {
                showToast(baseModel.toString());
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvPaymentMethod.setLayoutManager(linearLayoutManager);
        rcvPaymentMethod.setAdapter(paymentMethodAdapter);
    }

    private void displayProductCart() {
//        System.out.println(mProductCart.toString());
        ProductCheckoutAdapter productCheckoutAdapter = new ProductCheckoutAdapter(this, mProductCart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvProduct.setLayoutManager(linearLayoutManager);
        rcvProduct.setAdapter(productCheckoutAdapter);
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
            return;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initService() {
        apiService = RetrofitConnection.getApiService();
        checkoutPresenter = new CheckoutPresenter(this, apiService, mToken, mCustomer.get_id());
    }

    private void initEventClick() {

    }

    private void initUI() {
        rcvProduct = binding.rcvProduct;
        rcvPaymentMethod = binding.rcvOptionsPayments;
    }
}