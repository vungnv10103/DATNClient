package com.datn.client.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
        EdgeToEdge.enable(this);
        binding = ActivityEbankingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.e_banking), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();
        initEventClick();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
        checkLogin();
        initService();

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            // Nhận dữ liệu từ Broadcast
            String messageContent = intent.getStringExtra("message");
            MyDialog.gI().startDlgOKWithAction(EBankingActivity.this, messageContent, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showToast("312");
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Đăng ký BroadcastReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("com.datn.client.NOTIFICATION_RECEIVED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hủy đăng ký BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setLoading(true);
        ApiService apiService = RetrofitConnection.getApiService();
        String mProductID = getIntent().getStringExtra("productID");
        String mQuantity = getIntent().getStringExtra("quantity");
        if (mProductID == null || mQuantity == null) {
            createPaymentURL(apiService);
        } else {
            int quantity = Integer.parseInt(mQuantity);
            if (quantity <= 0) {
                MyDialog.gI().startDlgOKWithAction(EBankingActivity.this, getString(R.string.minimum_quantity_is_one), (dialog, which) -> finish());
                return;
            }
            createPaymentURLNow(apiService, mProductID, quantity);
        }
    }

    private void displayWebView(String paymentURL) {
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
    }

    private void handleResponseCreateURL(@NonNull Response<EBankingResponse> response) {
        if (response.body() != null) {
            runOnUiThread(() -> {
                String code = response.body().getCode();
                int statusCode = response.body().getStatusCode();
                if (statusCode == 200) {
                    Log.w(TAG, "onResponse200: createPaymentURL: " + code);
                    String paymentURL = response.body().getPaymentURL();
                    displayWebView(paymentURL);
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

    private void createPaymentURLNow(@NonNull ApiService apiService, String productID, int quantity) {
        try {
            Call<EBankingResponse> createPaymentURLNow = apiService.createPaymentURLNow(mToken, mCustomer.get_id(), productID, quantity, "", "vi");
            createPaymentURLNow.enqueue(new Callback<EBankingResponse>() {
                @Override
                public void onResponse(@NonNull Call<EBankingResponse> call, @NonNull Response<EBankingResponse> response) {
                    handleResponseCreateURL(response);
                }

                @Override
                public void onFailure(@NonNull Call<EBankingResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        MyDialog.gI().startDlgOK(EBankingActivity.this, t.getMessage());
                        setLoading(false);
                    });
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                Log.w(TAG, "createPaymentURLNow: " + e.getMessage());
                MyDialog.gI().startDlgOK(EBankingActivity.this, e.getMessage());
                setLoading(false);
            });
        }
    }

    private void createPaymentURL(ApiService apiService) {
        try {
            Call<EBankingResponse> createPaymentURL = apiService.createPaymentURL(mToken, mCustomer.get_id(), "", "vi");
            createPaymentURL.enqueue(new Callback<EBankingResponse>() {
                @Override
                public void onResponse(@NonNull Call<EBankingResponse> call, @NonNull Response<EBankingResponse> response) {
                    handleResponseCreateURL(response);
                }

                @Override
                public void onFailure(@NonNull Call<EBankingResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        MyDialog.gI().startDlgOK(EBankingActivity.this, t.getMessage());
                        setLoading(false);
                    });
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                Log.w(TAG, "createPaymentURL: " + e.getMessage());
                MyDialog.gI().startDlgOK(EBankingActivity.this, e.getMessage());
                setLoading(false);
            });
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

    private void switchToLogin() {
        showToast(getString(R.string.please_log_in_again));
        finishAffinity();
    }

    private void initEventClick() {

    }

    private void initService() {

    }
}