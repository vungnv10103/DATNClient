package com.datn.client.response;

import com.datn.client.models.ProductCart;

import java.util.List;

public class ProductCartResponse extends _BaseResponse {
    private List<ProductCart> productCarts;
    private String quantity;

    public List<ProductCart> getProductCarts() {
        return productCarts;
    }

    public void setProductCarts(List<ProductCart> productCarts) {
        this.productCarts = productCarts;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
