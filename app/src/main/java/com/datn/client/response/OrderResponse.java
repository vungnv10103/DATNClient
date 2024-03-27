package com.datn.client.response;

import com.datn.client.models.OrdersDetail;

public class OrderResponse extends _BaseResponse {
    private OrdersDetail ordersDetail;

    public OrdersDetail getOrdersDetail() {
        return ordersDetail;
    }

    public void setOrdersDetail(OrdersDetail ordersDetail) {
        this.ordersDetail = ordersDetail;
    }
}
