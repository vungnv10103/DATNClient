package com.datn.client.response;

import com.datn.client.models.Cart;
import com.datn.client.models.Product;

import java.util.List;

public class CartResponse extends _BaseResponse {
    private List<Cart> carts;
    private List<Product> products;

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
