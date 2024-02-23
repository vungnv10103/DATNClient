package com.datn.client.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.models.Product;
import com.datn.client.response.BannerResponse;
import com.datn.client.response.CategoryResponse;
import com.datn.client.response.ProductResponse;
import com.datn.client.services.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter {
    private static final String TAG = HomePresenter.class.getSimpleName();
    private final IHomeView iHomeView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    public HomePresenter(IHomeView iHomeView, ApiService apiService, String token, String customerID) {
        this.iHomeView = iHomeView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void getListBanner() {
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
                        Log.w(TAG, "onResponse: " + response);
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

    public void getListCategory() {
        try {
            Call<CategoryResponse> getCategory = apiService.getCategory(token, customerID);
            getCategory.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: " + response.body().getCode());
                            List<Category> data = response.body().getCategories();
                            iHomeView.onListCategory(data);
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: " + response.body().getCode());
                            iHomeView.onThrowMessage(response.body().getMessage());
                        }
                    } else {
                        Log.w(TAG, "onResponse: getListCategory: " + response);
                        iHomeView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                    iHomeView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getListCategory: " + e.getMessage());
            iHomeView.onThrowMessage(e.getMessage());
        }
    }

    public void getListSellingProduct() {
        try {
            Call<ProductResponse> getSellingProduct = apiService.getSellingProduct(token, customerID);
            getSellingProduct.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatusCode() == 200) {
                            Log.w(TAG, "onResponse200: " + response.body().getCode());
                            List<Product> data = response.body().getProducts();
                            iHomeView.onListSellingProduct(data);
                        } else if (response.body().getStatusCode() == 400) {
                            Log.w(TAG, "onResponse400: " + response.body().getCode());
                            iHomeView.onThrowMessage(response.body().getMessage());
                        }
                    } else {
                        Log.w(TAG, "onResponse: getListSellingProduct: " + response);
                        iHomeView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                    iHomeView.onThrowMessage(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getListSellingProduct: " + e.getMessage());
            iHomeView.onThrowMessage(e.getMessage());
        }
    }
}
