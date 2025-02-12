package com.datn.client.ui.order;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.BasePresenter;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.OrdersDetail;
import com.datn.client.response.OrderResponse;
import com.datn.client.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPresenter extends BasePresenter {
    private final FragmentActivity context;
    private final IOrderView iOrderView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private static Call<OrderResponse> getOrdersByStatus;

    public OrderPresenter(FragmentActivity context, IOrderView iOrderView, ApiService apiService, String token, String customerID) {
        super(context, iOrderView, apiService, token, customerID);
        this.context = context;
        this.iOrderView = iOrderView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    @Override
    public void onCancelAPI() {
        super.onCancelAPI();
        if (getOrdersByStatus != null) {
            getOrdersByStatus.cancel();
        }
    }


    public void getOrdersByStatus(int status) {
        context.runOnUiThread(() -> {
            try {
                getOrdersByStatus = apiService.getOrdersByStatus(token, customerID, status);
                getOrdersByStatus.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                OrdersDetail ordersDetail = response.body().getOrdersDetail();
                                if (ordersDetail != null) {
                                    iOrderView.onLoadOrders(ordersDetail);
                                }
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iOrderView.onFinish();
                                } else {
                                    iOrderView.onThrowLog("getAllOrders400", code);
                                    iOrderView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iOrderView.onThrowNotification("getOrdersByStatus: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                        iOrderView.onThrowNotification("getOrdersByStatus: : " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iOrderView.onThrowNotification("getOrdersByStatus: " + e.getMessage());
            }
        });
    }

}
