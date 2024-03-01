package com.datn.client.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.datn.client.R;
import com.datn.client.databinding.ActivityEbankingBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.ProductCart;
import com.datn.client.response.EBankingResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EBankingActivity extends AppCompatActivity {
    private static final String TAG = EBankingActivity.class.getSimpleName();
    private ActivityEbankingBinding binding;
    private PreferenceManager preferenceManager;

    private WebView webViewPay;

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
        binding = ActivityEbankingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

        ApiService apiService = RetrofitConnection.getApiService();

        try {
            Call<EBankingResponse> createPaymentURL = apiService.createPaymentURL(mToken, mCustomer.get_id(), "", "vi");
            createPaymentURL.enqueue(new Callback<EBankingResponse>() {
                @Override
                public void onResponse(@NonNull Call<EBankingResponse> call, @NonNull Response<EBankingResponse> response) {
                    if (response.body() != null) {
                        runOnUiThread(() -> {
                            String code = response.body().getCode();
                            int statusCode = response.body().getStatusCode();
                            if (statusCode == 200) {
                                Log.w(TAG, "onResponse200: createPaymentURL: " + code);
                                String paymentURL = response.body().getPaymentURL();
                                webViewPay.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                        String url = request.getUrl().toString();
                                        Log.w(TAG, "shouldOverrideUrlLoading: " + url);
                                        if (url.contains("/paySuccess")) {
                                            MyDialog.gI().startDlgOK(EBankingActivity.this, "paySuccess");
                                            return true;
                                        }
                                        if (url.contains("/payFail")) {
                                            MyDialog.gI().startDlgOK(EBankingActivity.this, "payFail");
                                            finish();
                                            return true;
                                        }
                                        return super.shouldOverrideUrlLoading(view, request);
                                    }
                                });
                                webViewPay.loadUrl(paymentURL);

                            } else if (statusCode == 400) {
                                Log.w(TAG, "onResponse400: createPaymentURL: " + code);
                                MyDialog.gI().startDlgOK(EBankingActivity.this, code);
                            } else {
                                Log.w(TAG, "onResponse: " + code);
                                MyDialog.gI().startDlgOK(EBankingActivity.this, code);
                            }
                        });
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        MyDialog.gI().startDlgOK(EBankingActivity.this, "body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EBankingResponse> call, @NonNull Throwable t) {
                    MyDialog.gI().startDlgOK(EBankingActivity.this, t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "createPaymentURL: " + e.getMessage());
            MyDialog.gI().startDlgOK(EBankingActivity.this, e.getMessage());
        }
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

    @SuppressLint("SetJavaScriptEnabled")
    private void initUI() {
        webViewPay = binding.webViewPay;
        WebSettings webSettings = webViewPay.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
    }

    private void initEventClick() {

    }

    private void initService() {

    }
}