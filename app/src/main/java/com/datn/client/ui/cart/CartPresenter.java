package com.datn.client.ui.cart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.ProductCart;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.services.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartPresenter {
    private static final String TAG = CartPresenter.class.getSimpleName();
    private final ICartView iCartView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    public CartPresenter(ICartView iCartView, ApiService apiService, String token, String customerID) {
        this.iCartView = iCartView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void getDataCart() {
        try {
            Call<ProductCartResponse> getCart = apiService.getCart(token, customerID);
            getCart.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: getDataCart: " + response.body().getCode());
                            List<ProductCart> dataProductCart = response.body().getProductCarts();
                            iCartView.onListCart(dataProductCart);
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: getDataCart: " + response.body().getCode());
                            iCartView.onThrowMessage(response.body().getCode());
                        } else {
                            Log.w(TAG, "onResponse: " + response.body().getCode());
                            iCartView.onThrowMessage(response.body().getCode());
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCartView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "getDataCart: " + t.getMessage());
                    iCartView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getDataCart: " + e.getMessage());
            iCartView.onThrowMessage(e.getMessage());
        }
    }

    public void updateQuantity(String cartID, String type, int quantity) {
        try {
            Call<ProductCartResponse> updateQuantity = apiService.updateQuantity(token, customerID, type, quantity, cartID);
            updateQuantity.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        String code = response.body().getCode();
                        int statusCode = response.body().getStatusCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: updateQuantity: " + code);
                            String quantity = response.body().getQuantity();
                            iCartView.onThrowMessage(response.body().getCode() + ":" + quantity);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: updateQuantity: " + code);
                            iCartView.onThrowMessage(response.body().getCode());
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCartView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCartView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "updateQuantity: " + t.getMessage());
                    iCartView.onThrowMessage(t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.w(TAG, "updateQuantity: " + e.getMessage());
            iCartView.onThrowMessage(e.getMessage());
        }
    }

    public void updateStatus(String cartID, int status) {
        try {
            Call<ProductCartResponse> updateStatus = apiService.updateStatus(token, customerID, status, cartID);
            updateStatus.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        String code = response.body().getCode();
                        int statusCode = response.body().getStatusCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: updateQuantity: " + code);
                            List<ProductCart> dataProductCart = response.body().getProductCarts();
                            iCartView.onListCart(dataProductCart);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: updateQuantity: " + code);
                            iCartView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCartView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCartView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "updateStatus: " + t.getMessage());
                    iCartView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "updateStatus: " + e.getMessage());
            iCartView.onThrowMessage(e.getMessage());
        }
    }

    public void updateStatusAll(boolean isSelected) {
        try {
            Call<ProductCartResponse> updateStatusAll = apiService.updateStatusAll(token, customerID, isSelected);
            updateStatusAll.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        String code = response.body().getCode();
                        int statusCode = response.body().getStatusCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: updateStatusAll: " + code);
                            List<ProductCart> dataProductCart = response.body().getProductCarts();
                            iCartView.onListCart(dataProductCart);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: updateStatusAll: " + code);
                            iCartView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCartView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCartView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "updateStatusAll: " + t.getMessage());
                    iCartView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "updateStatusAll: " + e.getMessage());
            iCartView.onThrowMessage(e.getMessage());
        }
    }
}
