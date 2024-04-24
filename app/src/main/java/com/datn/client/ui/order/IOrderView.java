package com.datn.client.ui.order;

import com.datn.client.IBaseView;
import com.datn.client.models.OrdersDetail;

public interface IOrderView extends IBaseView {
    void onLoadOrders(OrdersDetail ordersDetail);
}

