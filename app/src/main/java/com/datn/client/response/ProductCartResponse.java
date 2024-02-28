package com.datn.client.response;

import com.datn.client.models.ProductCart;

import java.util.List;

public class ProductCartResponse extends _BaseResponse {
    private List<ProductCart> productCarts;

    public List<ProductCart> getProductCarts() {
        return productCarts;
    }

    public void setProductCarts(List<ProductCart> productCarts) {
        this.productCarts = productCarts;
    }
}
