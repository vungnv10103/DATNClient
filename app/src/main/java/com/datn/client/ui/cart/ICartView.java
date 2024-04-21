package com.datn.client.ui.cart;

import com.datn.client.models.ProductCart;
import com.datn.client.IBaseView;

import java.util.List;

public interface ICartView extends IBaseView {
    void onListCart(List<ProductCart> productCartList);


    void onUpdateQuantity(String cartID, int position, String type, int value);

    void onUpdateStatus(String cartID, int position, int value);

    void onBuyNow(String cartID);
}
