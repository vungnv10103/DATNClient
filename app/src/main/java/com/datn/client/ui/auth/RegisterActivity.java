package com.datn.client.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.R;
import com.datn.client.databinding.ActivityRegisterBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.response.CustomerResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ActivityRegisterBinding binding;
    private VerifyOTPBottomSheet layoutVerify;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ImageButton imgBack;
    private Button btnRegister;
    private CircularProgressIndicator progressRegister;
    private TextInputEditText edEmail, edName, edPhone, edPass, edRePass;
    private TextView tvLogin;

    public boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initUI();
        initService();
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
        progressRegister.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void register() {
        try {
            String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
            String fullName = Objects.requireNonNull(edName.getText()).toString().trim();
            String phoneNumber = Objects.requireNonNull(edPhone.getText()).toString().trim();
            String password = Objects.requireNonNull(edPass.getText()).toString().trim();
            String rePassword = Objects.requireNonNull(edRePass.getText()).toString().trim();
            if (!password.matches(rePassword)) {
                showToast(getString(R.string.two_passwords_must_match));
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
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                Log.w(TAG, "onResponse200: " + code);
                                switch (code) {
                                    case "auth/verify":
                                        showToast(message.getContent());
                                        ManagerUser.gI().saveCustomerLogin(preferenceManager, response.body().getCustomer());
                                        layoutVerify = new VerifyOTPBottomSheet();
                                        layoutVerify.setCancelable(false);
                                        layoutVerify.show(getSupportFragmentManager(), VerifyOTPBottomSheet.TAG);
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
                            MyDialog.gI().startDlgOK(RegisterActivity.this, response.message());
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

    private boolean checkInputForm() {
        String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
        String fullName = Objects.requireNonNull(edName.getText()).toString().trim();
        String phoneNumber = Objects.requireNonNull(edPhone.getText()).toString().trim();
        String password = Objects.requireNonNull(edPass.getText()).toString().trim();
        String rePassword = Objects.requireNonNull(edRePass.getText()).toString().trim();
        if (email.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            showToast(getString(R.string.do_not_leave_it_blank));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            edEmail.setError(R.string.invalid_email));
            showToast(getString(R.string.invalid_email));
            return false;
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            showToast(getString(R.string.invalid_phone));
            return false;
        } else if (!password.matches(rePassword)) {
            showToast(getString(R.string.two_passwords_must_match));
            return false;
        }
        return true;
    }

    private void showToast(@NonNull Object message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
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
        binding.imgLogo.setOnLongClickListener(v -> {
            fakeData();
            return true;
        });
    }

    private void fakeData() {
        edEmail.setText(getString(R.string.test_email));
        edName.setText(getString(R.string.test_name));
        edPhone.setText(getString(R.string.test_phone));
        edPass.setText(getString(R.string.test_pass));
        edRePass.setText(getString(R.string.test_pass));
    }

    private void initUI() {
        TextView scrollingTextView = binding.tvMsg;
        scrollingTextView.setSelected(true);
        scrollingTextView.setSingleLine(true);
        imgBack = binding.imgBack;
        btnRegister = binding.btnRegister;
        progressRegister = binding.progressbarRegister;
        edEmail = binding.edEmail;
        edName = binding.edName;
        edPhone = binding.edPhone;
        edPass = binding.edPass;
        edRePass = binding.edRepass;
        tvLogin = binding.tvLogin;
        binding.imgLogo.setImageResource(Constants.isNightMode ? R.drawable.logo_app_white_no_bg : R.drawable.logo_app_gradient);
    }
}
