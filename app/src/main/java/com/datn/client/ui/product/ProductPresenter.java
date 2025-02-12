package com.datn.client.ui.product;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.BasePresenter;
import com.datn.client.models.Cart;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.Product;
import com.datn.client.models.ProductCart;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.response.ProductResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.checkout.CheckoutActivity;
import com.datn.client.utils.STATUS_CART;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductPresenter extends BasePresenter {
    private final FragmentActivity context;
    private final IProductView iProductView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<ProductResponse> detailProduct;
    private Call<_BaseResponse> addToCart;
    private Call<ProductResponse> getProductByCateID;
    private Call<ProductCartResponse> buyNow;

    public ProductPresenter(FragmentActivity context, IProductView iProductView, ApiService apiService, String token, String customerID) {
        super(context, iProductView, apiService, token, customerID);
        this.context = context;
        this.iProductView = iProductView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void onCancelAPI() {
        super.onCancelAPI();
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
        context.runOnUiThread(() -> {
            try {
                detailProduct = apiService.getDetailProduct(token, customerID, productID);
                detailProduct.enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iProductView.onThrowLog("getDetailProduct: onResponse200", code);
                                Product product = response.body().getProducts().get(0);
                                List<Product> data = new ArrayList<>();
                                data.add(product);
                                iProductView.onLoadProduct(data);
                            } else if (statusCode == 400) {
                                iProductView.onThrowLog("getDetailProduct: onResponse400", code);
                                iProductView.onThrowMessage(message);
                            } else {
                                iProductView.onThrowLog("getDetailProduct: onResponse", code);
                                iProductView.onThrowMessage(message);
                            }
                        } else {
                            iProductView.onThrowNotification("getDetailProduct: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                        iProductView.onThrowNotification("getDetailProduct: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iProductView.onThrowNotification("getDetailProduct: " + e.getMessage());
            }
        });
    }

    public void addToCart(String productID, int quantity, String notes) {
        context.runOnUiThread(() -> {
            try {
                Cart cart = new Cart(customerID, productID, quantity, STATUS_CART.DEFAULT.getValue(), notes);
                addToCart = apiService.addToCart(token, cart);
                addToCart.enqueue(new Callback<_BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iProductView.onThrowLog("addToCart: onResponse200", code);
                                iProductView.onThrowMessage(message);
                            } else if (statusCode == 400) {
                                iProductView.onThrowLog("addToCart: onResponse400", code);
                                iProductView.onThrowMessage(message);
                            } else {
                                iProductView.onThrowLog("addToCart: onResponse", code);
                                iProductView.onThrowMessage(message);
                            }
                        } else {
                            iProductView.onThrowNotification("addToCart: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                        iProductView.onThrowNotification("addToCart: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iProductView.onThrowNotification("addToCart: " + e.getMessage());
            }
        });
    }

    public void getProductByCateID(String categoryID) {
        context.runOnUiThread(() -> {
            try {
                getProductByCateID = apiService.getProductByCateID(token, customerID, categoryID);
                getProductByCateID.enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iProductView.onThrowLog("getProductByCateID: onResponse200", code);
                                List<Product> dataProduct = response.body().getProducts();
                                iProductView.onLoadProduct(dataProduct);
                            } else if (statusCode == 400) {
                                iProductView.onThrowLog("getProductByCateID: onResponse400", code);
                                iProductView.onThrowMessage(message);
                            } else {
                                iProductView.onThrowLog("getProductByCateID", code);
                            }
                        } else {
                            iProductView.onThrowNotification("getProductByCateID: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                        iProductView.onThrowNotification("getProductByCateID: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iProductView.onThrowNotification("getProductByCateID: " + e.getMessage());
            }
        });
    }

    public void buyNow(String productID, int quantity, String notes) {
        context.runOnUiThread(() -> {
            try {
                Cart cart = new Cart(customerID, productID, quantity, STATUS_CART.BUYING.getValue(), notes);
                buyNow = apiService.buyNow(token, cart);
                buyNow.enqueue(new Callback<ProductCartResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iProductView.onThrowLog("buyNow: onResponse200", code);
                                List<ProductCart> dataProduct = response.body().getProductCarts();
                                Intent intent = new Intent(context, CheckoutActivity.class);
                                intent.putExtra("productCart", (Serializable) dataProduct);
                                context.startActivity(intent);
                            } else if (statusCode == 400) {
                                iProductView.onThrowLog("buyNow: onResponse400", code);
                                iProductView.onThrowMessage(message);
                            } else {
                                iProductView.onThrowLog("buyNow", code);
                            }
                        } else {
                            iProductView.onThrowNotification("buyNow: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                        iProductView.onThrowNotification("buyNow: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iProductView.onThrowNotification("buyNow: " + e.getMessage());
            }
        });
    }
}
