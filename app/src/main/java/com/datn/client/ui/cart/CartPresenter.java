package com.datn.client.ui.cart;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.ProductCart;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.services.ApiService;
import com.datn.client.BasePresenter;
import com.datn.client.ui.checkout.CheckoutActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartPresenter extends BasePresenter {
    private final FragmentActivity context;
    private final ICartView iCartView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<ProductCartResponse> getCart;
    private Call<ProductCartResponse> updateQuantity;
    private Call<ProductCartResponse> updateStatus;
    private Call<ProductCartResponse> updateStatusAll;
    private Call<ProductCartResponse> buyNow;

    public CartPresenter(FragmentActivity context, ICartView iCartView, ApiService apiService, String token, String customerID) {
        super(context, iCartView, apiService, token, customerID);
        this.context = context;
        this.iCartView = iCartView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void onCancelAPI() {
        super.onCancelAPI();
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
        context.runOnUiThread(() -> {
            try {
                getCart = apiService.getCart(token, customerID);
                getCart.enqueue(new Callback<ProductCartResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCartView.onThrowLog("getDataCart: onResponse200", code);
                                List<ProductCart> dataProductCart = response.body().getProductCarts();
                                iCartView.onListCart(dataProductCart);
                            } else if (statusCode == 400) {
                                iCartView.onThrowLog("getDataCart: onResponse400", code);
                                iCartView.onThrowMessage(message);
                            } else {
                                iCartView.onThrowLog("getDataCart", code);
                                iCartView.onThrowMessage(message);
                            }
                        } else {
                            iCartView.onThrowNotification("getDataCart: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                        iCartView.onThrowNotification("getDataCart: onFailure" + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCartView.onThrowNotification("getDataCart: " + e.getMessage());
            }
        });
    }

    public void updateQuantity(String cartID, String type, int quantity) {
        context.runOnUiThread(() -> {
            try {
                updateQuantity = apiService.updateQuantity(token, customerID, type, quantity, cartID);
                updateQuantity.enqueue(new Callback<ProductCartResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                        if (response.body() != null) {
                            String code = response.body().getCode();
                            int statusCode = response.body().getStatusCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCartView.onThrowLog("updateQuantity: onResponse200", code);
                                List<ProductCart> dataProductCart = response.body().getProductCarts();
                                iCartView.onListCart(dataProductCart);
                            } else if (statusCode == 400) {
                                iCartView.onThrowLog("updateQuantity: onResponse400", code);
                                iCartView.onThrowMessage(message);
                            } else {
                                iCartView.onThrowLog("updateQuantity", code);
                                iCartView.onThrowMessage(message);
                            }
                        } else {
                            iCartView.onThrowNotification("updateQuantity: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                        iCartView.onThrowNotification("updateQuantity: onFailure: " + t.getMessage());
                    }
                });

            } catch (Exception e) {
                iCartView.onThrowNotification("updateQuantity: " + e.getMessage());
            }
        });
    }

    public void updateStatus(String cartID, int status) {
        context.runOnUiThread(() -> {
            try {
                updateStatus = apiService.updateStatus(token, customerID, status, cartID);
                updateStatus.enqueue(new Callback<ProductCartResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                        if (response.body() != null) {
                            String code = response.body().getCode();
                            int statusCode = response.body().getStatusCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCartView.onThrowLog("updateQuantity: onResponse200", code);
                                List<ProductCart> dataProductCart = response.body().getProductCarts();
                                iCartView.onListCart(dataProductCart);
                            } else if (statusCode == 400) {
                                iCartView.onThrowLog("updateQuantity: onResponse400", code);
                                iCartView.onThrowMessage(message);
                            } else {
                                iCartView.onThrowLog("updateQuantity", code);
                                iCartView.onThrowMessage(message);
                            }
                        } else {
                            iCartView.onThrowNotification("updateQuantity: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                        iCartView.onThrowNotification("updateQuantity: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCartView.onThrowNotification("updateQuantity: " + e.getMessage());
            }
        });
    }

    public void updateStatusAll(boolean isSelected) {
        context.runOnUiThread(() -> {
            try {
                updateStatusAll = apiService.updateStatusAll(token, customerID, isSelected);
                updateStatusAll.enqueue(new Callback<ProductCartResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCartView.onThrowLog("updateStatusAll: onResponse200", code);
                                List<ProductCart> dataProductCart = response.body().getProductCarts();
                                iCartView.onListCart(dataProductCart);
                            } else if (statusCode == 400) {
                                iCartView.onThrowLog("updateStatusAll: onResponse400", code);
                                iCartView.onThrowMessage(message);
                            } else {
                                iCartView.onThrowLog("updateStatusAll: onResponse", code);
                                iCartView.onThrowMessage(message);
                            }
                        } else {
                            iCartView.onThrowNotification("updateStatusAll: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                        iCartView.onThrowNotification("updateStatusAll: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCartView.onThrowNotification("updateStatusAll: " + e.getMessage());
            }
        });
    }

    public void buyNowCart(String cartID) {
        context.runOnUiThread(() -> {
            try {
                buyNow = apiService.buyNowCart(token, customerID, cartID);
                buyNow.enqueue(new Callback<ProductCartResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCartView.onThrowLog("buyNow: onResponse200", code);
                                // ArrayList<ProductCart> dataProductCart = (ArrayList<ProductCart>) response.body().getProductCarts();
                                Intent intent = new Intent(context, CheckoutActivity.class);
                                // intent.putParcelableArrayListExtra("productCart", dataProductCart);
                                context.startActivity(intent);
                            } else if (statusCode == 400) {
                                iCartView.onThrowLog("buyNow: onResponse400", code);
                                iCartView.onThrowMessage(message);
                            } else {
                                iCartView.onThrowLog("buyNow: onResponse", code);
                                iCartView.onThrowMessage(message);
                            }
                        } else {
                            iCartView.onThrowNotification("buyNow: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                        iCartView.onThrowNotification("buyNow: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCartView.onThrowNotification("buyNow: " + e.getMessage());
            }
        });
    }
}
