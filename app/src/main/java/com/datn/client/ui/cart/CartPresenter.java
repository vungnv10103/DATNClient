package com.datn.client.ui.cart;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.ProductCart;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.checkout.CheckoutActivity;

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

    private Call<ProductCartResponse> getCart;
    private Call<ProductCartResponse> updateQuantity;
    private Call<ProductCartResponse> updateStatus;
    private Call<ProductCartResponse> updateStatusAll;
    private Call<ProductCartResponse> buyNow;

    public CartPresenter(ICartView iCartView, ApiService apiService, String token, String customerID) {
        this.iCartView = iCartView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void cancelAPI() {
        if (getCart != null) {
            getCart.cancel();
        }
        if (updateQuantity != null) {
            updateQuantity.cancel();
        }
        if (updateStatus != null) {
            updateStatus.cancel();
        }
        if (updateStatusAll != null) {
            updateStatusAll.cancel();
        }
        if (buyNow != null) {
            buyNow.cancel();
        }

    }

    public void getDataCart() {
        try {
            getCart = apiService.getCart(token, customerID);
            getCart.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: getDataCart: " + code);
                            List<ProductCart> dataProductCart = response.body().getProductCarts();
                            iCartView.onListCart(dataProductCart);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: getDataCart: " + code);
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
            updateQuantity = apiService.updateQuantity(token, customerID, type, quantity, cartID);
            updateQuantity.enqueue(new Callback<ProductCartResponse>() {
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
            updateStatus = apiService.updateStatus(token, customerID, status, cartID);
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
            updateStatusAll = apiService.updateStatusAll(token, customerID, isSelected);
            updateStatusAll.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
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

    public void buyNowCart(Context context, String cartID) {
        try {
            buyNow = apiService.buyNowCart(token, customerID, cartID);
            buyNow.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: buyNow: " + code);
//                            ArrayList<ProductCart> dataProductCart = (ArrayList<ProductCart>) response.body().getProductCarts();
                            Intent intent = new Intent(context, CheckoutActivity.class);
//                            intent.putParcelableArrayListExtra("productCart", dataProductCart);
                            context.startActivity(intent);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: buyNow: " + code);
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
                    Log.w(TAG, "buyNow: " + t.getMessage());
                    iCartView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "buyNow: " + e.getMessage());
            iCartView.onThrowMessage(e.getMessage());
        }
    }
}
