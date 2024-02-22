package com.datn.client.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.response.BannerResponse;
import com.datn.client.services.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter {
    private static final String TAG = HomePresenter.class.getSimpleName();
    private final IHomeView iHomeView;
    private final ApiService apiService;
    private List<Banner> bannerList;
    private List<Category> categoryList;

    public HomePresenter(IHomeView iHomeView, ApiService apiService) {
        this.iHomeView = iHomeView;
        this.apiService = apiService;
    }

    public void getListBanner(String token, String customerID) {
        try {
            Call<BannerResponse> getBanner = apiService.getBanner(token, customerID);
            getBanner.enqueue(new Callback<BannerResponse>() {
                @Override
                public void onResponse(@NonNull Call<BannerResponse> call, @NonNull Response<BannerResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: " + response.body().getCode());
                            List<Banner> data = response.body().getBanners();
                            iHomeView.onListBanner(data);
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: " + response.body().getCode());
                            iHomeView.onThrowMessage(response.body().getMessage());
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response.toString());
                        iHomeView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BannerResponse> call, @NonNull Throwable t) {
                    iHomeView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getListBanner: " + e.getMessage());
            iHomeView.onThrowMessage(e.getMessage());
        }

    }
}
