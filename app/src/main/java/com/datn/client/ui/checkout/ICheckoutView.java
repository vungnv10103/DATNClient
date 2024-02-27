package com.datn.client.ui.checkout;

import com.datn.client.models.ProductCart;

import java.util.List;

public interface ICheckoutView {
    void onListProduct(List<ProductCart> productCartList);

    void onThrowMessage(String message);
}
