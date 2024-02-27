package com.datn.client.ui.checkout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.datn.client.R;
import com.datn.client.databinding.ActivityCheckoutBinding;
import com.datn.client.databinding.ActivityDetailProductBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.ProductCart;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.product.ProductPresenter;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity implements ICheckoutView {
    private ActivityCheckoutBinding binding;

    private ApiService apiService;
    private CheckoutPresenter checkoutPresenter;
    private PreferenceManager preferenceManager;

    private Customer mCustomer;
    private String mToken;


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
    }

    @Override
    public void onListProduct(List<ProductCart> productCartList) {
        showToast(productCartList.size() + "");
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

    }
}