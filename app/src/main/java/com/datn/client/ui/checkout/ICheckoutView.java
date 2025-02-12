package com.datn.client.ui.checkout;

import com.datn.client.IBaseView;
import com.datn.client.models.ProductCart;

import java.util.HashMap;
import java.util.List;

public interface ICheckoutView extends IBaseView {
    void onListProduct(List<ProductCart> productCartList);

    void onListPaymentMethod(HashMap<Integer, String> paymentMethod);

    void onCreateOrder(String amount);

}
