package com.datn.client.ui.product;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.Cart;
import com.datn.client.models.Product;
import com.datn.client.response.BaseResponse;
import com.datn.client.response.ProductResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.home.HomePresenter;
import com.datn.client.ui.home.IHomeView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductPresenter {
    private static final String TAG = ProductPresenter.class.getSimpleName();
    private final IProductView iProductView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    public ProductPresenter(IProductView iProductView, ApiService apiService, String token, String customerID) {
        this.iProductView = iProductView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void getDetailProduct(String productID) {
        try {
            Call<ProductResponse> detailProduct = apiService.getDetailProduct(token, customerID, productID);
            detailProduct.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: getDetailProduct: " + response.body().getCode());
                            Product product = response.body().getProducts().get(0);
                            iProductView.onLoadProduct(product);
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: getDetailProduct: " + response.body().getCode());
                        } else {
                            Log.w(TAG, "onResponse: " + response.body().getCode());
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iProductView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                    iProductView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getDetailProduct: " + e.getMessage());
            iProductView.onThrowMessage(e.getMessage());
        }
    }

    public void addToCart(String productID, int quantity, String notes) {
        try {
            Cart cart = new Cart(customerID, productID, quantity, 1, notes);
            Call<BaseResponse> addToCart = apiService.addToCart(token, cart);
            addToCart.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: addToCart: " + response.body().getCode());
                            iProductView.onThrowMessage(response.body().getCode());
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: addToCart: " + response.body().getCode());
                            iProductView.onThrowMessage(response.body().getCode());
                        } else {
                            Log.w(TAG, "onResponse: " + response.body().getCode());
                            iProductView.onThrowMessage(response.body().getCode());
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iProductView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                    iProductView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "addToCart: " + e.getMessage());
            iProductView.onThrowMessage(e.getMessage());
        }
    }
}
