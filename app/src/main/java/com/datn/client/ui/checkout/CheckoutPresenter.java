package com.datn.client.ui.checkout;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.ProductCart;
import com.datn.client.response.PaymentMethodResponse;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.cart.CartPresenter;
import com.datn.client.ui.cart.ICartView;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutPresenter {
    private static final String TAG = CheckoutPresenter.class.getSimpleName();
    private final ICheckoutView iCheckoutView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    public CheckoutPresenter(ICheckoutView iCheckoutView, ApiService apiService, String token, String customerID) {
        this.iCheckoutView = iCheckoutView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void getProductCheckout() {
        try {
            Call<ProductCartResponse> getProductCheckout = apiService.getProductCheckout(token, customerID);
            getProductCheckout.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: getProductCheckout: " + code);
                            List<ProductCart> dataProductCart = response.body().getProductCarts();
                            iCheckoutView.onListProduct(dataProductCart);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: getProductCheckout: " + code);
                            iCheckoutView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCheckoutView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCheckoutView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "getProductCheckout: " + t.getMessage());
                    iCheckoutView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getProductCheckout: " + e.getMessage());
            iCheckoutView.onThrowMessage(e.getMessage());
        }
    }

    public void getPaymentMethod() {
        try {
            Call<PaymentMethodResponse> getPaymentMethod = apiService.getPaymentMethod(token, customerID);
            getPaymentMethod.enqueue(new Callback<PaymentMethodResponse>() {
                @Override
                public void onResponse(@NonNull Call<PaymentMethodResponse> call, @NonNull Response<PaymentMethodResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: getPaymentMethod: " + code);
                            HashMap<Integer, String> dataPayment = response.body().getPaymentMethod();
                            iCheckoutView.onListPaymentMethod(dataPayment);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: getPaymentMethod: " + code);
                            iCheckoutView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCheckoutView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCheckoutView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PaymentMethodResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "getPaymentMethod: " + t.getMessage());
                    iCheckoutView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getPaymentMethod: " + e.getMessage());
            iCheckoutView.onThrowMessage(e.getMessage());
        }
    }

    public enum PAYMENT_METHOD {
        ON_DELIVERY(0),
        E_BANKING(1),
        ZALO_PAY(2);
        private final int value;

        PAYMENT_METHOD(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }
}
