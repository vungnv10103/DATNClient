package com.datn.client.ui.auth;

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

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.MainActivity;
import com.datn.client.R;
import com.datn.client.databinding.ActivityLoginBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.response.CustomerResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActivityLoginBinding binding;
    private VerifyOTPBottomSheet layoutVerify;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private RelativeLayout layoutLogin;
    private CircularProgressIndicator progressBarLoading, progressBarLogin;
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
                    .setTitle(TAG.split("Activity")[0])
                    .setMessage(getString(R.string.are_you_sure_exit))
                    .setIcon(R.drawable.logo_app_gradient)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                    .setNegativeButton(android.R.string.no, null).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();
        initService();
        initEventClick();
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isRemember = preferenceManager.getBoolean("isRemember");
        if (isRemember) {
            mCustomer = getLogin();
            cbRemember.setChecked(true);
            edEmail.setText(mCustomer.getEmail());
            edPass.setText(mCustomer.getPassword());
            checkLogin();
        } else {
            progressBarLoading.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        }
    }

    private void checkLogin() {
        setLoading(true);
        runOnUiThread(() -> {
            try {
                String token = preferenceManager.getString("token");
                mCustomer.setToken(token);
                Call<_BaseResponse> checkLogin = apiService.checkLogin(token, mCustomer);
                checkLogin.enqueue(new Callback<_BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                        runOnUiThread(() -> {
                            if (response.body() != null) {
                                int statusCode = response.body().getStatusCode();
                                String code = response.body().getCode();
                                MessageResponse message = response.body().getMessage();
                                if (statusCode == 200) {
                                    showLogW("onResponse200", code);
                                    switch (code) {
                                        case "auth/200":
                                            managerLoading(true);
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finishAffinity();
                                            break;
                                        case "":
                                        default:
                                            managerLoading(false);
                                            MyDialog.gI().startDlgOK(LoginActivity.this, message.getContent());
                                    }
                                } else if (statusCode == 400) {
                                    managerLoading(false);
                                    showLogW("onResponse400", code);
                                    switch (code) {
                                        case "auth/wrong-token":
                                        case "jwt expired":
                                            //preferenceManager.putBoolean("isRemember", false);
                                            MyDialog.gI().startDlgOK(LoginActivity.this, getString(R.string.session_expired));
                                            preferenceManager.putString("token", "");
                                            break;
                                        default:
                                            MyDialog.gI().startDlgOK(LoginActivity.this, message.getContent());
                                            break;
                                    }
                                }

                            } else {
                                managerLoading(false);
                                MyDialog.gI().startDlgOK(LoginActivity.this, response.message());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                        runOnUiThread(() -> {
                            managerLoading(false);
                            showLogW("checkLogin: onFailure", t.getMessage());
                            MyDialog.gI().startDlgOK(LoginActivity.this, t.getMessage());
                        });
                    }
                });
            } catch (Exception e) {
                managerLoading(false);
                showLogW("checkLogin", e.getMessage());
                MyDialog.gI().startDlgOK(LoginActivity.this, e.getMessage());
            }
        });
    }

    private void managerLoading(boolean isLogin) {
        if (!isLogin) {
            setLoading(false);
            progressBarLoading.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        }
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void showLogW(String key, String message) {
        runOnUiThread(() -> Log.w(TAG, key + ": " + message));
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private boolean checkInputForm() {
        String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(edPass.getText()).toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            showToast(getString(R.string.do_not_leave_it_blank));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            edEmail.setError(R.string.invalid_email);
            showToast(getString(R.string.invalid_email));
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
        runOnUiThread(() -> {
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
                                MessageResponse message = response.body().getMessage();
                                if (statusCode == 200) {
                                    showLogW("onResponse200", code);
                                    switch (code) {
                                        case "auth/verify":
                                            preferenceManager.putBoolean("isRemember", cbRemember.isChecked());
                                            saveLogin(response.body().getCustomer());
                                            layoutVerify = new VerifyOTPBottomSheet();
                                            layoutVerify.setCancelable(false);
                                            layoutVerify.show(getSupportFragmentManager(), VerifyOTPBottomSheet.TAG);
//                                            startActivity(new Intent(LoginActivity.this, VerifyOTPActivity.class));
                                            break;
                                        case "auth/verify-phone":
                                        case "auth/no-verify":
                                        default:
                                            runOnUiThread(() -> {
                                                setLoading(false);
                                                MyDialog.gI().startDlgOK(LoginActivity.this, message.getContent());
                                            });
                                            break;
                                    }
                                } else if (statusCode == 400) {
                                    runOnUiThread(() -> {
                                        setLoading(false);
                                        showLogW("onResponse400", code);
                                        MyDialog.gI().startDlgOK(LoginActivity.this, message.getContent());
                                    });
                                }
                            } else {
                                runOnUiThread(() -> {
                                    setLoading(false);
                                    MyDialog.gI().startDlgOK(LoginActivity.this, "body null");
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<CustomerResponse> call, @NonNull Throwable t) {
                        runOnUiThread(() -> {
                            setLoading(false);
                            MyDialog.gI().startDlgOK(LoginActivity.this, t.getMessage());
                        });
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    showLogW("login", e.getMessage());
                });
            }
        });

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
        runOnUiThread(() -> {
            this.isLoading = isLoading;
            edEmail.setFocusable(!isLoading);
            edPass.setFocusable(!isLoading);
            if (!isLoading) {
                edEmail.setFocusableInTouchMode(true);
                edPass.setFocusableInTouchMode(true);
            }
            progressBarLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnLogin.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });
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
        progressBarLogin = binding.progressbarLogin;
        progressBarLoading = binding.progressbarLoading;
        edEmail = binding.edEmail;
        edPass = binding.edPass;
        btnLogin = binding.btnLogin;
        cbRemember = binding.cbRemember;
    }
}
