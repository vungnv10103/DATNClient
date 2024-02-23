package com.datn.client.ui.cart;

import com.datn.client.services.ApiService;
import com.datn.client.ui.home.HomePresenter;
import com.datn.client.ui.home.IHomeView;

public class CartPresenter {
    private static final String TAG = CartPresenter.class.getSimpleName();
    private final ICartView iCartView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    public CartPresenter(ICartView iCartView, ApiService apiService, String token, String customerID) {
        this.iCartView = iCartView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }
}
