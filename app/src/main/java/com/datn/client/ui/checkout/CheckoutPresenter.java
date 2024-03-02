package com.datn.client.ui.checkout;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.CartBuyNow;
import com.datn.client.models.ProductCart;
import com.datn.client.response.CreateOrderResponse;
import com.datn.client.response.PaymentMethodResponse;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;

import java.util.HashMap;
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

    private Call<ProductCartResponse> getProductCheckout;
    private Call<PaymentMethodResponse> getPaymentMethod;
    private Call<CreateOrderResponse> createOrder;
    private Call<CreateOrderResponse> getAmountZaloPay;
    private Call<_BaseResponse> createOrderZaloPay;
    private Call<_BaseResponse> createOrderZaloPayNow;

    public CheckoutPresenter(ICheckoutView iCheckoutView, ApiService apiService, String token, String customerID) {
        this.iCheckoutView = iCheckoutView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void cancelAPI() {
        if (getProductCheckout != null) {
            getProductCheckout.cancel();
        }
        if (getPaymentMethod != null) {
            getPaymentMethod.cancel();
        }
        if (createOrder != null) {
            createOrder.cancel();
        }
        if (getAmountZaloPay != null) {
            getAmountZaloPay.cancel();
        }
        if (createOrderZaloPay != null) {
            createOrderZaloPay.cancel();
        }
        if (createOrderZaloPayNow != null) {
            createOrderZaloPayNow.cancel();
        }
    }

    public void getProductCheckout() {
        try {
            getProductCheckout = apiService.getProductCheckout(token, customerID);
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
                        iCheckoutView.onThrowMessage(response.toString());
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
            getPaymentMethod = apiService.getPaymentMethod(token, customerID);
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
                        iCheckoutView.onThrowMessage(response.toString());
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


    public void getAmountZaloPay(int type) {
        try {
            createOrder = apiService.getAmountZaloPay(token, customerID, type);
            createOrder.enqueue(new Callback<CreateOrderResponse>() {
                @Override
                public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: getAmountZaloPay: " + code);
                            String amount = response.body().getAmount();
                            iCheckoutView.onCreateOrder(amount);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: getAmountZaloPay: " + code);
                            iCheckoutView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCheckoutView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCheckoutView.onThrowMessage(response.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "getAmountZaloPay: " + t.getMessage());
                    iCheckoutView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getAmountZaloPay: " + e.getMessage());
            iCheckoutView.onThrowMessage(e.getMessage());
        }
    }

    public void getAmountZaloPay(int type, List<ProductCart> productCartList) {
        try {
            CartBuyNow cartBuyNow = new CartBuyNow();
            cartBuyNow.setCustomerID(customerID);
            cartBuyNow.setType(type);
            cartBuyNow.setProductCarts(productCartList);
            getAmountZaloPay = apiService.getAmountZaloPayNow(token, cartBuyNow);
            getAmountZaloPay.enqueue(new Callback<CreateOrderResponse>() {
                @Override
                public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: getAmountZaloPay: " + code);
                            String amount = response.body().getAmount();
                            iCheckoutView.onCreateOrder(amount);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: getAmountZaloPay: " + code);
                            iCheckoutView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCheckoutView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCheckoutView.onThrowMessage(response.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "getAmountZaloPay: " + t.getMessage());
                    iCheckoutView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getAmountZaloPay: " + e.getMessage());
            iCheckoutView.onThrowMessage(e.getMessage());
        }
    }

    public void createOrderZaloPay() {
        try {
            createOrderZaloPay = apiService.createOrderZaloPay(token, customerID);
            createOrderZaloPay.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: createOrderZaloPay: " + code);
                            iCheckoutView.onThrowMessage(code);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: createOrderZaloPay: " + code);
                            iCheckoutView.onThrowMessage(code);
                        } else {
                            Log.w(TAG, "onResponse: " + code);
                            iCheckoutView.onThrowMessage(code);
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response);
                        iCheckoutView.onThrowMessage(response.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "createOrderZaloPay: " + t.getMessage());
                    iCheckoutView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "createOrderZaloPay: " + e.getMessage());
            iCheckoutView.onThrowMessage(e.getMessage());
        }
    }

    public void createOrderZaloPayNow(List<ProductCart> productCartList) {
        try {
            CartBuyNow cartBuyNow = new CartBuyNow();
            cartBuyNow.setCustomerID(customerID);
            cartBuyNow.setProductCarts(productCartList);
            createOrderZaloPayNow = apiService.createOrderZaloPayNow(token, cartBuyNow);
            createOrderZaloPayNow.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: createOrderZaloPayNow: " + code);
                            iCheckoutView.onThrowMessage(code);
                        } else if (statusCode == 400) {
                            Log.w(TAG, "onResponse400: createOrderZaloPayNow: " + code);
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
                public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                    Log.w(TAG, "createOrderZaloPayNow: " + t.getMessage());
                    iCheckoutView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "createOrderZaloPayNow: " + e.getMessage());
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
