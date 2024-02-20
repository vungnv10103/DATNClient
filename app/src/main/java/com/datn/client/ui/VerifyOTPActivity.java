package com.datn.client.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.client.MainActivity;
import com.datn.client.databinding.ActivityVerifyOtpactivityBinding;
import com.datn.client.models.Customer;
import com.datn.client.response.BaseResponse;
import com.datn.client.response.VerifyResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTPActivity extends AppCompatActivity {
    private static final String TAG = VerifyOTPActivity.class.getSimpleName();
    private ActivityVerifyOtpactivityBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private SpinKitView spinKitView;

    private EditText edOTP1, edOTP2, edOTP3, edOTP4, edOTP5, edOTP6;
    private Button btnVerify;

    public boolean isLoading = false;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            new AlertDialog.Builder(VerifyOTPActivity.this)
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
        binding = ActivityVerifyOtpactivityBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);
        initUI();

        initService();

        initEventClick();

    }


    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        edOTP1.setFocusable(!isLoading);
        edOTP2.setFocusable(!isLoading);
        edOTP3.setFocusable(!isLoading);
        edOTP4.setFocusable(!isLoading);
        edOTP5.setFocusable(!isLoading);
        edOTP6.setFocusable(!isLoading);
        if (!isLoading) {
            edOTP1.setFocusableInTouchMode(true);
            edOTP2.setFocusableInTouchMode(true);
            edOTP3.setFocusableInTouchMode(true);
            edOTP4.setFocusableInTouchMode(true);
            edOTP5.setFocusableInTouchMode(true);
            edOTP6.setFocusableInTouchMode(true);
        }
        spinKitView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnVerify.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void saveLogin(Customer customer) {
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        preferenceManager.putString("user", json);
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void verify() {
        try {
            String OTP = edOTP1.getText().toString().trim() + edOTP2.getText().toString().trim()
                    + edOTP3.getText().toString().trim() + edOTP4.getText().toString().trim()
                    + edOTP5.getText().toString().trim() + edOTP6.getText().toString().trim();

            String tempID = "65d46c4deac37188dd85e5c2";
            Customer customer = new Customer(tempID, "", OTP);
            Call<VerifyResponse> verify = apiService.verify(customer);
            verify.enqueue(new Callback<VerifyResponse>() {
                @Override
                public void onResponse(@NonNull Call<VerifyResponse> call, @NonNull Response<VerifyResponse> response) {
                    runOnUiThread(() -> {
                        if (response.body() != null) {
                            if (response.body().getStatusCode() == 200) {
                                Log.w(TAG, "onResponse200: " + response.body().getCode());
                                String message;
                                switch (response.body().getCode()) {
                                    case "auth/login-success":
                                        saveLogin(response.body().getCustomer());
                                        startActivity(new Intent(VerifyOTPActivity.this, MainActivity.class));
                                        finishAffinity();
                                        break;
                                    case "auth/wrong-otp":
                                    default:
                                        message = response.body().getMessage();
                                        MyDialog.gI().startDlgOK(VerifyOTPActivity.this, response.body().getMessage());
                                }
                                setLoading(false);
                            } else if (response.body().getStatusCode() == 400) {
                                Log.w(TAG, "onResponse400: " + response.body().getCode());
                            }
                        } else {
                            MyDialog.gI().startDlgOK(VerifyOTPActivity.this, "body null");
                            setLoading(false);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<VerifyResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        MyDialog.gI().startDlgOK(VerifyOTPActivity.this, t.getMessage());
                        setLoading(false);
                    });
                }
            });

        } catch (Exception e) {
            Log.w(TAG, "verify: " + e.getMessage());
            setLoading(false);
        }
    }

    private void doVerify() {
        setLoading(true);
        boolean isValid = validateOTP();
        if (!isValid) {
            setLoading(false);
        } else {
            verify();
        }
    }


    private void initService() {
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
    }

    private void initEventClick() {
        btnVerify.setOnClickListener(v -> {
            if (!isLoading) {
                doVerify();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateOTP() {
        if (edOTP1.getText().toString().trim().isEmpty() ||
                edOTP2.getText().toString().trim().isEmpty() ||
                edOTP3.getText().toString().trim().isEmpty() ||
                edOTP4.getText().toString().trim().isEmpty() ||
                edOTP5.getText().toString().trim().isEmpty() ||
                edOTP6.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng không bỏ trống!");
            return false;
        }
        return true;
    }


    private void initUI() {
        spinKitView = binding.spinKit;
        btnVerify = binding.btnVerify;
        edOTP1 = binding.edOtp1;
        edOTP2 = binding.edOtp2;
        edOTP3 = binding.edOtp3;
        edOTP4 = binding.edOtp4;
        edOTP5 = binding.edOtp5;
        edOTP6 = binding.edOtp6;
        fillInputOTP();
    }

    private void fillInputOTP() {

        edOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edOTP2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edOTP3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edOTP4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edOTP5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edOTP6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}