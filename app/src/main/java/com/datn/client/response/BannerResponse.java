package com.datn.client.response;

import com.datn.client.models.Banner;

import java.util.List;

public class BannerResponse extends _BaseResponse {
    private List<Banner> banners;

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
}
