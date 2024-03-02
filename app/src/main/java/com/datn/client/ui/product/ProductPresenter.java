package com.datn.client.ui.product;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.Cart;
import com.datn.client.models.Product;
import com.datn.client.models.ProductCart;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.response.ProductResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.checkout.CheckoutActivity;

import java.io.Serializable;
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

    private Call<ProductResponse> detailProduct;
    private Call<_BaseResponse> addToCart;
    private Call<ProductResponse> getProductByCateID;
    private Call<ProductCartResponse> buyNow;

    public ProductPresenter(IProductView iProductView, ApiService apiService, String token, String customerID) {
        this.iProductView = iProductView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void cancelAPI() {
        if (detailProduct != null) {
            detailProduct.cancel();
        }
        if (addToCart != null) {
            addToCart.cancel();
        }
        if (getProductByCateID != null) {
            getProductByCateID.cancel();
        }
        if (buyNow != null) {
            buyNow.cancel();
        }
    }

    public void getDetailProduct(String productID) {
        try {
            detailProduct = apiService.getDetailProduct(token, customerID, productID);
            detailProduct.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: getDetailProduct: " + code);
                            Product product = response.body().getProducts().get(0);
                            List<Product> data = new ArrayList<>();
                            data.add(product);
                            iProductView.onLoadProduct(data);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: getDetailProduct: " + code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iProductView.onThrowMessage(response.toString());
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
            addToCart = apiService.addToCart(token, cart);
            addToCart.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: addToCart: " + code);
                            iProductView.onThrowMessage(code);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: addToCart: " + code);
                            iProductView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iProductView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iProductView.onThrowMessage(response.toString());
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
            getProductByCateID = apiService.getProductByCateID(token, customerID, categoryID);
            getProductByCateID.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: getProductByCateID: " + code);
                            List<Product> dataProduct = response.body().getProducts();
                            iProductView.onLoadProduct(dataProduct);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: getProductByCateID: " + code);
                            iProductView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iProductView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iProductView.onThrowMessage(response.toString());
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

    public void buyNow(Context context, String productID, int quantity, String notes) {
        try {
            Cart cart = new Cart(customerID, productID, quantity, STATUS_CART.BUYING.getValue(), notes);
            buyNow = apiService.buyNow(token, cart);
            buyNow.enqueue(new Callback<ProductCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: buyNow: " + code);
                            List<ProductCart> dataProduct = response.body().getProductCarts();
                            Intent intent = new Intent(context, CheckoutActivity.class);
                            intent.putExtra("productCart", (Serializable) dataProduct);
                            context.startActivity(intent);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: buyNow: " + code);
                            iProductView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iProductView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iProductView.onThrowMessage(response.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                    iProductView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "buyNow: " + e.getMessage());
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
        BOUGHT(2),
        BUYING(3);
        private final int value;

        STATUS_CART(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }
}
