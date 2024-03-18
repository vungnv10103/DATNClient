package com.datn.client.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.client.MainActivity;
import com.datn.client.databinding.ActivityLoginBinding;
import com.datn.client.models.Customer;
import com.datn.client.response._BaseResponse;
import com.datn.client.response.CustomerResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActivityLoginBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private RelativeLayout layoutLogin;
    private SpinKitView spinKitView, spinKitLoading;
    private TextInputEditText edEmail, edPass;
    private CheckBox cbRemember;
    private Button btnLogin;
    private TextView tvForgetPass, tvRegister;
    private Customer mCustomer;

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
        initService();
        initEventClick();

        boolean isRemember = preferenceManager.getBoolean("isRemember");
        if (isRemember) {
            mCustomer = getLogin();
            cbRemember.setChecked(true);
            edEmail.setText(mCustomer.getEmail());
            edPass.setText(mCustomer.getPassword());
            checkLogin();
        } else {
            spinKitLoading.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        }
    }

    private void checkLogin() {
        setLoading(true);
        mCustomer.setToken(preferenceManager.getString("token"));
        try {
            Call<_BaseResponse> checkLogin = apiService.checkLogin(mCustomer);
            checkLogin.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                    runOnUiThread(() -> {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            String message = response.body().getMessage();
                            if (statusCode == 200) {
                                Log.w(TAG, "onResponse200: " + code);
                                switch (code) {
                                    case "auth/200":
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finishAffinity();
                                        break;
                                    case "":
                                    default:
                                        MyDialog.gI().startDlgOK(LoginActivity.this, message);
                                }
                                setLoading(false);
                            } else if (statusCode == 400) {
                                Log.w(TAG, "onResponse400: " + code);
                                MyDialog.gI().startDlgOK(LoginActivity.this, message);
                                if (code.equals("auth/wrong-token")) {
//                                    preferenceManager.putBoolean("isRemember", false);
                                    preferenceManager.putString("token", "");
                                }
                            }
                        } else {
                            MyDialog.gI().startDlgOK(LoginActivity.this, "body null");
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        MyDialog.gI().startDlgOK(LoginActivity.this, t.getMessage());
                    });
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "checkLogin: " + e.getMessage());
            MyDialog.gI().startDlgOK(LoginActivity.this, e.getMessage());
        } finally {
            setLoading(false);
            spinKitLoading.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        }

    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    private void saveLogin(Customer customer) {
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        preferenceManager.putString("user", json);
    }

    private void login() {
        try {
            String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(edPass.getText()).toString().trim();
            Customer customer = new Customer(email, password);
            Call<CustomerResponse> login = apiService.loginCustomer(customer);
            login.enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(@NonNull Call<CustomerResponse> call, @NonNull Response<CustomerResponse> response) {
                    runOnUiThread(() -> {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            String message = response.body().getMessage();
                            if (statusCode == 200) {
                                Log.w(TAG, "onResponse200: " + code);
                                switch (code) {
                                    case "auth/verify":
                                        preferenceManager.putBoolean("isRemember", cbRemember.isChecked());
                                        saveLogin(response.body().getCustomer());
                                        startActivity(new Intent(LoginActivity.this, VerifyOTPActivity.class));
                                        break;
                                    case "auth/verify-phone":
                                    case "auth/no-verify":
                                    default:
                                        MyDialog.gI().startDlgOK(LoginActivity.this, message);
                                }
                                setLoading(false);
                            } else if (statusCode == 400) {
                                Log.w(TAG, "onResponse400: " + code);
                                MyDialog.gI().startDlgOK(LoginActivity.this, message);
                            }
                        } else {
                            MyDialog.gI().startDlgOK(LoginActivity.this, "body null");
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<CustomerResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        MyDialog.gI().startDlgOK(LoginActivity.this, t.getMessage());
                    });
                }
            });

        } catch (Exception e) {
            Log.w(TAG, "login: " + e.getMessage());
        } finally {
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
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
    }

    private void initEventClick() {
        tvRegister.setOnClickListener(v -> {
            if (!isLoading) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(v -> doLogin());
        tvForgetPass.setOnClickListener(v -> {

        });
    }

    private void initUI() {
        layoutLogin = binding.layoutLogin;
        tvForgetPass = binding.tvForgotPass;
        tvRegister = binding.tvRegister;
        spinKitView = binding.spinKit;
        spinKitLoading = binding.spinKitLoading;
        edEmail = binding.edEmail;
        edPass = binding.edPass;
        btnLogin = binding.btnLogin;
        cbRemember = binding.cbRemember;
    }

}