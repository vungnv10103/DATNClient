package com.datn.client.ui.home;

import com.datn.client.models.Banner;
import com.datn.client.models.Category;

import java.util.List;

public interface IHomeView {
    void onListBanner(List<Banner> bannerList);
    void onListCategory(List<Category> categoryList);
    void onThrowMessage(String message);
}
