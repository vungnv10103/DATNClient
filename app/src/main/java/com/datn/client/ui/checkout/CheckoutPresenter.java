package com.datn.client.ui.checkout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.CartBuyNow;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.ProductCart;
import com.datn.client.response.CreateOrderResponse;
import com.datn.client.response.PaymentMethodResponse;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.BasePresenter;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutPresenter extends BasePresenter {
    private final FragmentActivity context;
    private final ICheckoutView iCheckoutView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<_BaseResponse> createOrderDelivery;
    private Call<ProductCartResponse> getProductCheckout;
    private Call<PaymentMethodResponse> getPaymentMethod;
    private Call<CreateOrderResponse> createOrder;
    private Call<CreateOrderResponse> getAmountZaloPay;
    private Call<_BaseResponse> createOrderZaloPay;
    private Call<_BaseResponse> createOrderZaloPayNow;

    public CheckoutPresenter(FragmentActivity context, ICheckoutView iCheckoutView, ApiService apiService, String token, String customerID) {
        super(context, iCheckoutView, apiService, token, customerID);
        this.context = context;
        this.iCheckoutView = iCheckoutView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void cancelAPI() {
        super.cancelAPI();
        if (getProductCheckout != null) {
            getProductCheckout.cancel();
        }
        if (getPaymentMethod != null) {
            getPaymentMethod.cancel();
        }
        if (createOrder != null) {
            createOrder.cancel();
        }
        if (createOrderDelivery != null) {
            createOrderDelivery.cancel();
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

    public void createOrderDelivery() {
        context.runOnUiThread(() -> {
            try {
                createOrderDelivery = apiService.createOrderDelivery(token, customerID);
                createOrderDelivery.enqueue(new Callback<_BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCheckoutView.onThrowLog("createOrderDelivery: onResponse200", code);
                                iCheckoutView.onThrowMessage(message);
                            } else if (statusCode == 400) {
                                iCheckoutView.onThrowLog("createOrderDelivery: onResponse400", code);
                                iCheckoutView.onThrowMessage(message);
                            } else {
                                iCheckoutView.onThrowLog("createOrderDelivery: onResponse", code);
                                iCheckoutView.onThrowMessage(message);
                            }
                        } else {
                            iCheckoutView.onThrowLog("createOrderDelivery: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                        iCheckoutView.onThrowLog("createOrderDelivery", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCheckoutView.onThrowLog("createOrderDelivery", e.getMessage());
            }

        });
    }

    public void getProductCheckout() {
        context.runOnUiThread(() -> {
            try {
                getProductCheckout = apiService.getProductCheckout(token, customerID);
                getProductCheckout.enqueue(new Callback<ProductCartResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCheckoutView.onThrowLog("getProductCheckout: onResponse200", code);
                                List<ProductCart> dataProductCart = response.body().getProductCarts();
                                iCheckoutView.onListProduct(dataProductCart);
                            } else if (statusCode == 400) {
                                iCheckoutView.onThrowLog("getProductCheckout: onResponse400", code);
                                iCheckoutView.onThrowMessage(message);
                            } else {
                                iCheckoutView.onThrowLog("getProductCheckout: onResponse", code);
                                iCheckoutView.onThrowMessage(message);
                            }
                        } else {
                            iCheckoutView.onThrowLog("getProductCheckout: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                        iCheckoutView.onThrowLog("getProductCheckout", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCheckoutView.onThrowLog("getProductCheckout", e.getMessage());
            }
        });
    }

    public void getPaymentMethod() {
        context.runOnUiThread(() -> {
            try {
                getPaymentMethod = apiService.getPaymentMethod(token, customerID);
                getPaymentMethod.enqueue(new Callback<PaymentMethodResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PaymentMethodResponse> call, @NonNull Response<PaymentMethodResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCheckoutView.onThrowLog("getPaymentMethod: onResponse200", code);
                                HashMap<Integer, String> dataPayment = response.body().getPaymentMethod();
                                iCheckoutView.onListPaymentMethod(dataPayment);
                            } else if (statusCode == 400) {
                                iCheckoutView.onThrowLog("getPaymentMethod: onResponse400", code);
                                iCheckoutView.onThrowMessage(message);
                            } else {
                                iCheckoutView.onThrowLog("getPaymentMethod: onResponse", code);
                                iCheckoutView.onThrowMessage(message);
                            }
                        } else {
                            iCheckoutView.onThrowLog("getPaymentMethod", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaymentMethodResponse> call, @NonNull Throwable t) {
                        iCheckoutView.onThrowLog("getPaymentMethod", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCheckoutView.onThrowLog("getPaymentMethod", e.getMessage());
            }
        });
    }


    public void getAmountZaloPay() {
        context.runOnUiThread(() -> {
            try {
                createOrder = apiService.getAmountZaloPay(token, customerID, PAYMENT_METHOD.ZALO_PAY.getValue());
                createOrder.enqueue(new Callback<CreateOrderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCheckoutView.onThrowLog("getAmountZaloPay: onResponse200", code);
                                String amount = response.body().getAmount();
                                iCheckoutView.onCreateOrder(amount);
                            } else if (statusCode == 400) {
                                iCheckoutView.onThrowLog("getAmountZaloPay: onResponse400", code);
                                iCheckoutView.onThrowMessage(message);
                            } else {
                                iCheckoutView.onThrowLog("getAmountZaloPay: onResponse", code);
                                iCheckoutView.onThrowMessage(message);
                            }
                        } else {
                            iCheckoutView.onThrowLog("getAmountZaloPay: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                        iCheckoutView.onThrowLog("getAmountZaloPay", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCheckoutView.onThrowLog("getAmountZaloPay", e.getMessage());
            }
        });
    }

    public void getAmountZaloPay(List<ProductCart> productCartList) {
        context.runOnUiThread(() -> {
            try {
                CartBuyNow cartBuyNow = new CartBuyNow();
                cartBuyNow.setCustomerID(customerID);
                cartBuyNow.setType(PAYMENT_METHOD.ZALO_PAY.getValue());
                cartBuyNow.setProductCarts(productCartList);
                getAmountZaloPay = apiService.getAmountZaloPayNow(token, cartBuyNow);
                getAmountZaloPay.enqueue(new Callback<CreateOrderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCheckoutView.onThrowLog("getAmountZaloPay: onResponse200", code);
                                String amount = response.body().getAmount();
                                iCheckoutView.onCreateOrder(amount);
                            } else if (statusCode == 400) {
                                iCheckoutView.onThrowLog("getAmountZaloPay: onResponse400", code);
                                iCheckoutView.onThrowMessage(message);
                            } else {
                                iCheckoutView.onThrowLog("getAmountZaloPay: onResponse", code);
                                iCheckoutView.onThrowMessage(message);
                            }
                        } else {
                            iCheckoutView.onThrowLog("getAmountZaloPay", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                        iCheckoutView.onThrowLog("getAmountZaloPay", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCheckoutView.onThrowLog("getAmountZaloPay", e.getMessage());
            }
        });
    }

    public void createOrderZaloPay() {
        context.runOnUiThread(() -> {
            try {
                createOrderZaloPay = apiService.createOrderZaloPay(token, customerID);
                createOrderZaloPay.enqueue(new Callback<_BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCheckoutView.onThrowLog("createOrderZaloPay: onResponse200", code);
                                //MessageResponse messageResponse = new MessageResponse(code,);
                                iCheckoutView.onThrowMessage(message);
                            } else if (statusCode == 400) {
                                iCheckoutView.onThrowLog("createOrderZaloPay: onResponse400", code);
                                iCheckoutView.onThrowMessage(message);
                            } else {
                                iCheckoutView.onThrowLog("createOrderZaloPay: onResponse", code);
                                iCheckoutView.onThrowMessage(message);
                            }
                        } else {
                            iCheckoutView.onThrowLog("createOrderZaloPay", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                        iCheckoutView.onThrowLog("createOrderZaloPay", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCheckoutView.onThrowLog("createOrderZaloPay", e.getMessage());
            }
        });
    }

    public void createOrderZaloPayNow(List<ProductCart> productCartList) {
        context.runOnUiThread(() -> {
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
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iCheckoutView.onThrowLog("createOrderZaloPayNow: onResponse200", code);
                                iCheckoutView.onThrowMessage(message);
                            } else if (statusCode == 400) {
                                iCheckoutView.onThrowLog("createOrderZaloPayNow: onResponse400", code);
                                iCheckoutView.onThrowMessage(message);
                            } else {
                                iCheckoutView.onThrowLog("createOrderZaloPayNow: onResponse", code);
                                iCheckoutView.onThrowMessage(message);
                            }
                        } else {
                            iCheckoutView.onThrowLog("createOrderZaloPayNow", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                        iCheckoutView.onThrowLog("createOrderZaloPayNow", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iCheckoutView.onThrowLog("createOrderZaloPayNow", e.getMessage());
            }
        });
    }

    public enum PAYMENT_METHOD {
        DELIVERY(0),
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
