package com.datn.client.ui.product;

import com.datn.client.models.Product;
import com.datn.client.IBaseView;

import java.util.List;

public interface IProductView extends IBaseView {
    void onLoadProduct(List<Product> productList);
}
