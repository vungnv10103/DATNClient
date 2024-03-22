package com.datn.client.ui.home;

import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Product;
import com.datn.client.ui.IBaseView;

import java.util.List;

public interface IHomeView extends IBaseView {
    void onListBanner(List<Banner> bannerList);

    void onListCategory(List<Category> categoryList);

    void onListSellingProduct(List<Product> productList);

}
