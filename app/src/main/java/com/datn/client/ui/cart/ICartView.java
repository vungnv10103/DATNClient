package com.datn.client.ui.cart;

import com.datn.client.models.Banner;
import com.datn.client.models.Cart;

import java.util.List;

public interface ICartView {
    void onListCart(List<Cart> cartList);

    void onThrowMessage(String message);
}
