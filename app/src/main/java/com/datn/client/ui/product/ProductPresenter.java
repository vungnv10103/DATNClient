package com.datn.client.ui.product;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.Cart;
import com.datn.client.models.Product;
import com.datn.client.response._BaseResponse;
import com.datn.client.response.ProductResponse;
import com.datn.client.services.ApiService;

import java.util.ArrayList;
import java.util.List;

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
                            List<Product> data = new ArrayList<>();
                            data.add(product);
                            iProductView.onLoadProduct(data);
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
            Cart cart = new Cart(customerID, productID, quantity, STATUS_CART.DEFAULT.getValue(), notes);
            Call<_BaseResponse> addToCart = apiService.addToCart(token, cart);
            addToCart.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
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
                public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                    iProductView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "addToCart: " + e.getMessage());
            iProductView.onThrowMessage(e.getMessage());
        }
    }

    public void getProductByCateID(String categoryID) {
        try {
            Call<ProductResponse> getProductByCateID = apiService.getProductByCateID(token, customerID, categoryID);
            getProductByCateID.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: getProductByCateID: " + response.body().getCode());
                            List<Product> dataProduct = response.body().getProducts();
                            iProductView.onLoadProduct(dataProduct);
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: getProductByCateID: " + response.body().getCode());
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
                public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                    iProductView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getProductByCateID: " + e.getMessage());
            iProductView.onThrowMessage(e.getMessage());
        }
    }

    public enum STATUS_PRODUCT {
        OUT_OF_STOCK(0),
        STOCKING(1);
        private final int value;

        STATUS_PRODUCT(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }


    public enum STATUS_CART {
        DELETED(-1),
        DEFAULT(0),
        SELECTED(1),
        BUYING(2);
        private final int value;

        STATUS_CART(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }
}
