package com.datn.client.ui.checkout;

import com.datn.client.models.MessageResponse;
import com.datn.client.models.ProductCart;

import java.util.HashMap;
import java.util.List;

public interface ICheckoutView {
    void onListProduct(List<ProductCart> productCartList);

    void onListPaymentMethod(HashMap<Integer, String> paymentMethod);

    void onCreateOrder(String amount);

    void onThrowMessage(MessageResponse message);

    void onThrowMessage(String message);
}
