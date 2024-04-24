package com.datn.client.ui.home;

import com.datn.client.IBaseView;
import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.models.Product;

import java.util.List;

public interface IHomeView extends IBaseView {
    void onListBanner(List<Banner> bannerList);

    void onListCategory(List<Category> categoryList);

    void onListSellingProduct(List<Product> productList);

    void onSearchProduct(List<Product> productList);
}
