package com.datn.client.ui.order;

import com.datn.client.models.OrdersDetail;
import com.datn.client.ui.IBaseView;

public interface IOrderView extends IBaseView {
    void onLoadOrders(OrdersDetail ordersDetail);
}

