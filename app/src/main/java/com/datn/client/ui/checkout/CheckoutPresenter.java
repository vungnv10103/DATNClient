package com.datn.client.ui.checkout;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.ProductCart;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.cart.CartPresenter;
import com.datn.client.ui.cart.ICartView;

import java.util.List;

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
                            iCheckoutView.onThrowMessage(response.body().getCode());
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
}
