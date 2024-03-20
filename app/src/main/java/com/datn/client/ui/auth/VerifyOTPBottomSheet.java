package com.datn.client.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.datn.client.MainActivity;
import com.datn.client.R;
import com.datn.client.databinding.BottomsheetVerifyOtpBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.response.CustomerResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTPBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "VerifyBottomSheet";
    private BottomsheetVerifyOtpBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private CircularProgressIndicator progressBarLoading;

    private EditText edOTP1, edOTP2, edOTP3, edOTP4, edOTP5, edOTP6;
    private Button btnVerify;
    private Customer mCustomer;

    public boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomsheetVerifyOtpBinding.inflate(getLayoutInflater());
        initUI();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initService();
        initEventClick();
        mCustomer = getLogin();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = task.getResult();
            preferenceManager.putString("fcm", token);
        });
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
        progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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

    private void addTokenFMC(String token, @NonNull Customer cus) {
        try {
            String fcm = preferenceManager.getString("fcm");
            Customer customer = new Customer(cus.get_id(), cus.getPassword(), false);
            customer.setFcm(fcm);
            Call<_BaseResponse> addFCM = apiService.addFCM(token, customer);
            addFCM.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                    requireActivity().runOnUiThread(() -> {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                switch (code) {
                                    case "auth/add-fcm-success":
                                        showToast(message.getContent());
                                        requireDialog().dismiss();
                                        startActivity(new Intent(requireActivity(), MainActivity.class));
                                        requireActivity().finishAffinity();
                                        break;
                                    case "":
                                    default:
                                        requireActivity().runOnUiThread(() -> {
                                            setLoading(false);
                                            MyDialog.gI().startDlgOK(requireActivity(), message.getContent());
                                        });
                                        break;
                                }
                            } else if (statusCode == 400) {
                                requireActivity().runOnUiThread(() -> {
                                    setLoading(false);
                                    MyDialog.gI().startDlgOK(requireActivity(), code);
                                });
                            }

                        } else {
                            requireActivity().runOnUiThread(() -> {
                                setLoading(false);
                                MyDialog.gI().startDlgOK(requireActivity(), "body null");
                            });
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                    requireActivity().runOnUiThread(() -> {
                        setLoading(false);
                        MyDialog.gI().startDlgOK(requireActivity(), t.getMessage());
                    });
                }
            });
        } catch (Exception e) {
            requireActivity().runOnUiThread(() -> {
                setLoading(false);
                Log.w(TAG, "addTokenFMC: " + e.getMessage());
                MyDialog.gI().startDlgOK(requireActivity(), e.getMessage());
            });
        }
    }

    private void verify() {
        try {
            String OTP = edOTP1.getText().toString().trim() + edOTP2.getText().toString().trim()
                    + edOTP3.getText().toString().trim() + edOTP4.getText().toString().trim()
                    + edOTP5.getText().toString().trim() + edOTP6.getText().toString().trim();

            if (mCustomer == null) {
                showToast(getString(R.string.please_log_in_again));
                requireActivity().finishAffinity();
                return;
            }
            Customer customer = new Customer(mCustomer.get_id(), mCustomer.getPassword(), false);
            customer.setOtp(OTP);
            Call<CustomerResponse> verify = apiService.verify(customer);
            verify.enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(@NonNull Call<CustomerResponse> call, @NonNull Response<CustomerResponse> response) {
                    requireActivity().runOnUiThread(() -> {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                Log.w(TAG, "onResponse200: " + code);
                                switch (code) {
                                    case "auth/login-success":
                                        saveLogin(response.body().getCustomer());
                                        String token = response.body().getToken();
                                        preferenceManager.putString("token", token);
                                        addTokenFMC(token, response.body().getCustomer());
                                        break;
                                    case "auth/wrong-otp":
                                    default:
                                        requireActivity().runOnUiThread(() -> {
                                            setLoading(false);
                                            MyDialog.gI().startDlgOK(requireActivity(), message.getContent());
                                        });

                                }
                            } else if (statusCode == 400) {
                                requireActivity().runOnUiThread(() -> {
                                    Log.w(TAG, "onResponse400: " + code);
                                    setLoading(false);
                                    MyDialog.gI().startDlgOK(requireActivity(), code);
                                });
                            }
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                setLoading(false);
                                MyDialog.gI().startDlgOK(requireActivity(), "body null");
                            });
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<CustomerResponse> call, @NonNull Throwable t) {
                    requireActivity().runOnUiThread(() -> {
                        setLoading(false);
                        MyDialog.gI().startDlgOK(requireActivity(), t.getMessage());
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
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
    }

    private void initEventClick() {
        btnVerify.setOnClickListener(v -> {
            if (!isLoading) {
                doVerify();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateOTP() {
        if (edOTP1.getText().toString().trim().isEmpty() ||
                edOTP2.getText().toString().trim().isEmpty() ||
                edOTP3.getText().toString().trim().isEmpty() ||
                edOTP4.getText().toString().trim().isEmpty() ||
                edOTP5.getText().toString().trim().isEmpty() ||
                edOTP6.getText().toString().trim().isEmpty()) {
            showToast(getString(R.string.do_not_leave_it_blank));
            return false;
        }
        return true;
    }


    private void initUI() {
        progressBarLoading = binding.progressbarVerify;
        btnVerify = binding.btnVerify;
        edOTP1 = binding.edOtp1;
        edOTP2 = binding.edOtp2;
        edOTP3 = binding.edOtp3;
        edOTP4 = binding.edOtp4;
        edOTP5 = binding.edOtp5;
        edOTP6 = binding.edOtp6;
        edOTP1.requestFocus();
        fillInputOTP();
    }

    private void fillInputOTP() {

        edOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    edOTP2.requestFocus();
                }
            }
        });
        edOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    edOTP3.requestFocus();
                } else {
                    edOTP1.requestFocus();
                }
            }
        });
        edOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    edOTP4.requestFocus();
                } else {
                    edOTP2.requestFocus();
                }
            }
        });
        edOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    edOTP5.requestFocus();
                } else {
                    edOTP3.requestFocus();
                }
            }
        });
        edOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    edOTP6.requestFocus();
                } else {
                    edOTP4.requestFocus();
                }
            }
        });
        edOTP6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    View view = requireDialog().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    edOTP5.requestFocus();
                }
            }
        });
    }
}