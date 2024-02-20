package com.datn.client.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.client.databinding.ActivityLoginBinding;
import com.datn.client.models.Customer;
import com.datn.client.response.BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActivityLoginBinding binding;
    private ApiService apiService;
    private SpinKitView spinKitView;
    private TextInputEditText edEmail, edPass;
    private Button btnLogin;
    private TextView tvRegister;

    public boolean isLoading = false;


    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Title")
                    .setMessage("Do you really want to whatever?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        System.out.println("OnBackPressed");
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);
        initUI();

        fakeData();

        initService();

        initEventClick();


    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void fakeData() {
        edEmail.setText("vungnguyenn1001@gmail.com");
        edPass.setText("Vung@123");
    }

    private boolean checkInputForm() {
        String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(edPass.getText()).toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng không để trống!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            edEmail.setError("Email không hợp lệ!");
            showToast("Email không hợp lệ!");
            return false;
        }
        return true;
    }

    private void login() {
        try {
            String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(edPass.getText()).toString().trim();
            Customer customer = new Customer(email, password);
            Call<BaseResponse> login = apiService.loginCustomer(customer);
            login.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                    runOnUiThread(() -> {
                        if (response.body() != null) {
                            if (response.body().getStatusCode() == 200) {
                                Log.w(TAG, "onResponse200: " + response.body().getCode());
                                String message;
                                switch (response.body().getCode()) {
                                    case "auth/verify":
                                        startActivity(new Intent(LoginActivity.this, VerifyOTPActivity.class));
                                        finishAffinity();
                                        break;
                                    case "auth/verify-phone":
                                    case "auth/no-verify":
                                    default:
                                        message = response.body().getMessage();
                                        MyDialog.gI().startDlgOK(LoginActivity.this, message);
                                }
                                setLoading(false);
                            } else if (response.body().getStatusCode() == 400) {
                                Log.w(TAG, "onResponse400: " + response.body().getCode());
                            }

                        } else {
                            MyDialog.gI().startDlgOK(LoginActivity.this, "body null");
                            setLoading(false);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        MyDialog.gI().startDlgOK(LoginActivity.this, t.getMessage());
                        setLoading(false);
                    });
                }
            });


        } catch (Exception e) {
            Log.w(TAG, "login: " + e.getMessage());
            setLoading(false);
        }
    }

    private void doLogin() {
        setLoading(true);
        boolean isValid = checkInputForm();
        if (!isValid) {
            setLoading(false);
        } else {
            login();
        }
    }


    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        edEmail.setFocusable(!isLoading);
        edPass.setFocusable(!isLoading);
        if (!isLoading) {
            edEmail.setFocusableInTouchMode(true);
            edPass.setFocusableInTouchMode(true);
        }
        spinKitView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void initService() {
        apiService = RetrofitConnection.getApiService();
    }

    private void initEventClick() {
        tvRegister.setOnClickListener(v -> {
            if (!isLoading) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void initUI() {
        tvRegister = binding.tvRegister;
        spinKitView = binding.spinKit;
        edEmail = binding.edEmail;
        edPass = binding.edPass;
        btnLogin = binding.btnLogin;
    }


}