package com.datn.client.ui.home;

import com.datn.client.models.Banner;

import java.util.List;

public interface IHomeView {
    void onListBanner(List<Banner> bannerList);
    void onThrowMessage(String message);
}
