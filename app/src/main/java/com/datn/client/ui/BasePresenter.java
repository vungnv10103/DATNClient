package com.datn.client.ui;

import com.datn.client.services.ApiService;
import com.datn.client.ui.cart.ICartView;

public class BasePresenter {
    private static final String TAG = BasePresenter.class.getSimpleName();
    private final ICartView iCartView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    public BasePresenter(ICartView iCartView, ApiService apiService, String token, String customerID) {
        this.iCartView = iCartView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }
}
