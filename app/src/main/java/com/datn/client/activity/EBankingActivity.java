package com.datn.client.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.client.R;
import com.datn.client.databinding.ActivityEbankingBinding;
import com.datn.client.models.Customer;
import com.datn.client.response.EBankingResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EBankingActivity extends AppCompatActivity {
    private static final String TAG = EBankingActivity.class.getSimpleName();
    private ActivityEbankingBinding binding;
    private PreferenceManager preferenceManager;

    private WebView webViewPay;
    private CircularProgressIndicator progressLoading;

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

        setLoading(true);

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
                                            MyDialog.gI().startDlgOKWithAction(EBankingActivity.this, "paySuccess", (dialog, which) -> finish());
                                            return true;
                                        }
                                        if (url.contains("/payFail")) {
                                            MyDialog.gI().startDlgOKWithAction(EBankingActivity.this, "payFail", (dialog, which) -> finish());
                                            return true;
                                        }
                                        return super.shouldOverrideUrlLoading(view, request);
                                    }
                                });
                                webViewPay.loadUrl(paymentURL);
                                setLoading(false);
                            } else if (statusCode == 400) {
                                Log.w(TAG, "onResponse400: createPaymentURL: " + code);
                                MyDialog.gI().startDlgOKWithAction(EBankingActivity.this, code, (dialog, which) -> finish());
                                setLoading(false);
                            } else {
                                Log.w(TAG, "onResponse: " + code);
                                MyDialog.gI().startDlgOK(EBankingActivity.this, code);
                                setLoading(false);
                            }
                        });
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        MyDialog.gI().startDlgOK(EBankingActivity.this, "body null");
                        setLoading(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EBankingResponse> call, @NonNull Throwable t) {
                    MyDialog.gI().startDlgOK(EBankingActivity.this, t.getMessage());
                    setLoading(false);
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "createPaymentURL: " + e.getMessage());
            MyDialog.gI().startDlgOK(EBankingActivity.this, e.getMessage());
            setLoading(false);
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
            switchToLogin();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            switchToLogin();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setLoading(boolean isLoading) {
        progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        webViewPay.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUI() {
        progressLoading = binding.progressLoading;
        webViewPay = binding.webViewPay;
        WebSettings webSettings = webViewPay.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
    }

    private void switchToLogin(){
        showToast(getString(R.string.please_log_in_again));
        finishAffinity();
    }

    private void initEventClick() {

    }

    private void initService() {

    }
}