package com.datn.client.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.client.databinding.ActivityRegisterBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ActivityRegisterBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ImageButton imgBack;
    private Button btnRegister;
    private SpinKitView spinKitView;
    private TextInputEditText edEmail, edName, edPhone, edPass, edRePass;
    private TextView tvLogin;

    public boolean isLoading = false;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            new AlertDialog.Builder(RegisterActivity.this)
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
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        Objects.requireNonNull(getSupportActionBar()).hide();
        initUI();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initService();

        fakeData();
        initEventClick();
    }

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        edEmail.setFocusable(!isLoading);
        edName.setFocusable(!isLoading);
        edPhone.setFocusable(!isLoading);
        edPass.setFocusable(!isLoading);
        edRePass.setFocusable(!isLoading);
        if (!isLoading) {
            edEmail.setFocusableInTouchMode(true);
            edName.setFocusableInTouchMode(true);
            edPhone.setFocusableInTouchMode(true);
            edPass.setFocusableInTouchMode(true);
            edRePass.setFocusableInTouchMode(true);
        }
        spinKitView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void saveLogin(Customer customer) {
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        preferenceManager.putString("user", json);
    }

    private void register() {
        try {
            String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
            String fullName = Objects.requireNonNull(edName.getText()).toString().trim();
            String phoneNumber = Objects.requireNonNull(edPhone.getText()).toString().trim();
            String password = Objects.requireNonNull(edPass.getText()).toString().trim();
            String rePassword = Objects.requireNonNull(edRePass.getText()).toString().trim();
            if (!password.matches(rePassword)) {
                showToast("2 password must matches");
                return;
            }
            Customer customer = new Customer(email, password, fullName, phoneNumber);
            Call<CustomerResponse> registerCustomer = apiService.registerCustomer(customer);
            registerCustomer.enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(@NonNull Call<CustomerResponse> call, @NonNull Response<CustomerResponse> response) {
                    runOnUiThread(() -> {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                Log.w(TAG, "onResponse200: " + code);
                                switch (code) {
                                    case "auth/verify":
                                        showToast(message.getContent());
                                        saveLogin(response.body().getCustomer());
                                        startActivity(new Intent(RegisterActivity.this, VerifyOTPActivity.class));
                                        break;
                                    case "":
                                    default:
                                        break;
                                }
                                MyDialog.gI().startDlgOK(RegisterActivity.this, message.getContent());
                            } else if (statusCode == 400) {
                                Log.w(TAG, "onResponse400: " + code);
                                switch (code) {
                                    case "auth/missing-email":
                                    case "auth/missing-fullname":
                                    case "auth/phone-exists":
                                    case "auth/email-exists":
                                    default:
                                        break;
                                }
                                MyDialog.gI().startDlgOK(RegisterActivity.this, message.getContent());
                            }
                        } else {
                            MyDialog.gI().startDlgOK(RegisterActivity.this, "body null");
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<CustomerResponse> call, @NonNull Throwable t) {
                    MyDialog.gI().startDlgOK(RegisterActivity.this, t.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    @SuppressLint("SetTextI18n")
    private void fakeData() {
        edEmail.setText("vungnguyenn1001@gmail.com");
        edName.setText("Vững Nguyễn");
        edPhone.setText("0366112725");
        edPass.setText("Vung@123");
        edRePass.setText("Vung@123");
    }

    private boolean checkInputForm() {
        String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
        String fullName = Objects.requireNonNull(edName.getText()).toString().trim();
        String phoneNumber = Objects.requireNonNull(edPhone.getText()).toString().trim();
        String password = Objects.requireNonNull(edPass.getText()).toString().trim();
        String rePassword = Objects.requireNonNull(edRePass.getText()).toString().trim();
        if (email.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            showToast("Vui lòng không để trống!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            edEmail.setError("Email không hợp lệ!");
            showToast("Email không hợp lệ!");
            return false;
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            showToast("Số điện thoại không hợp lệ");
            return false;
        } else if (!password.matches(rePassword)) {
            showToast("2 password must matches");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void doRegister() {
        setLoading(true);
        boolean isValid = checkInputForm();
        if (!isValid) {
            setLoading(false);
        } else {
            register();
        }
    }

    private void initService() {
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
    }

    private void initEventClick() {
        imgBack.setOnClickListener(v -> {
            if (!isLoading) {
                finish();
            }
        });
        btnRegister.setOnClickListener(v -> {
            if (!isLoading) {
                doRegister();
            }
        });
        tvLogin.setOnClickListener(v -> {
            if (!isLoading) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });
    }

    private void initUI() {
        TextView scrollingTextView = binding.tvMsg;
        scrollingTextView.setSelected(true);
        scrollingTextView.setSingleLine(true);
        imgBack = binding.imgBack;
        btnRegister = binding.btnRegister;
        spinKitView = binding.spinKit;
        edEmail = binding.edEmail;
        edName = binding.edName;
        edPhone = binding.edPhone;
        edPass = binding.edPass;
        edRePass = binding.edRepass;
        tvLogin = binding.tvLogin;
    }


}