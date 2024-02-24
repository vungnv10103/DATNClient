package com.datn.client.ui.product;

import com.datn.client.models.Product;

import java.util.List;

public interface IProductView {
    void onLoadProduct(Product product);

    void onThrowMessage(String message);
}
