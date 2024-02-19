package com.datn.client.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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

import com.datn.client.R;
import com.datn.client.databinding.ActivityRegisterBinding;
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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ActivityRegisterBinding binding;
    private ApiService apiService;
    private ImageButton imgBack;
    private Button btnRegister;
    private SpinKitView spinKitView;
    private TextInputEditText edEmail, edName, edPhone, edPass, edRePass;
    private TextView tvLogin;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("Title")
                    .setMessage("Do you really want to whatever?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            System.out.println("OnBackPressed");
                            finish();
                        }
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

        apiService = RetrofitConnection.getApiService();

        imgBack.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> doRegister());
        fakeData();
    }

    private void setLoading(boolean isLoading) {
        spinKitView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void register() {
        try {
            String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
            String fullName = Objects.requireNonNull(edName.getText()).toString().trim();
            String phoneNumber = Objects.requireNonNull(edPhone.getText()).toString().trim();
            String password = Objects.requireNonNull(edPass.getText()).toString().trim();
            String rePassword = Objects.requireNonNull(edRePass.getText()).toString().trim();

            Customer customer = new Customer(email, password, fullName, phoneNumber);
            Call<BaseResponse> registerCustomer = apiService.registerCustomer(customer);
            registerCustomer.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: " + response.body().getCode());
                            String message = "";
                            switch (response.body().getCode()) {
                                case "auth/verify":
                                    message = response.body().getMessage();
                                    break;
                                case "":
                                    break;
                                default:
                                    message = response.body().getCode();
                                    break;
                            }
                            MyDialog.gI().startDlgOK(RegisterActivity.this, message);
                            setLoading(false);
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: " + response.body().getCode());
                            String message = "";
                            switch (response.body().getCode()) {
                                case "auth/missing-email":
                                    message = "Missing email";
                                    break;
                                case "auth/missing-fullname":
                                    message = "Missing full_name";
                                    break;
                                case "auth/phone-exists":
                                    message = getString(R.string.phone_exists);
                                    break;
                                case "auth/email-exists":
                                    message = getString(R.string.email_exists);
                                    break;
                                default:
                                    message = "undefined";
                                    break;
                            }
                            MyDialog.gI().startDlgOK(RegisterActivity.this, message);
                            setLoading(false);
                        }
                    } else {
                        MyDialog.gI().startDlgOK(RegisterActivity.this, "body null");
                        setLoading(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                    MyDialog.gI().startDlgOK(RegisterActivity.this, t.getMessage());
                    setLoading(false);
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            setLoading(false);
        }
    }

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
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Email không hợp lệ!", Drawable.createFromPath(""));
            return false;
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
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
            showToast("Điền hợp lệ!");
        } else {
            register();
        }
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