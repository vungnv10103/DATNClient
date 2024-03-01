package com.datn.client.ui.cart;

import com.datn.client.models.Banner;
import com.datn.client.models.Cart;
import com.datn.client.models.Product;
import com.datn.client.models.ProductCart;

import java.util.List;

public interface ICartView {
    void onListCart(List<ProductCart> productCartList);

    void onThrowMessage(String message);
    void onUpdateQuantity(String cartID, int position, String type, int value);
    void onUpdateStatus(String cartID, int position, int value);
    void onBuyNow(String cartID);
}
