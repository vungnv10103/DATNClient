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

    private Call<BannerResponse> getBanner;
    private Call<CategoryResponse> getCategory;
    private Call<ProductResponse> getSellingProduct;

    public HomePresenter(IHomeView iHomeView, ApiService apiService, String token, String customerID) {
        this.iHomeView = iHomeView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void cancelAPI() {
        if (getBanner != null) {
            getBanner.cancel();
        }
        if (getCategory != null) {
            getCategory.cancel();
        }
        if (getSellingProduct != null) {
            getSellingProduct.cancel();
        }
    }

    public void getListBanner() {
        try {
            getBanner = apiService.getBanner(token, customerID);
            getBanner.enqueue(new Callback<BannerResponse>() {
                @Override
                public void onResponse(@NonNull Call<BannerResponse> call, @NonNull Response<BannerResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: " + code);
                            List<Banner> data = response.body().getBanners();
                            iHomeView.onListBanner(data);
                        } else if (statusCode == 400) {
                            if (code.equals("auth/wrong-token")) {
                                iHomeView.onFinish();
                            } else {
                                Log.w(TAG, "onResponse400: " + code);
                                iHomeView.onThrowMessage(response.body().getMessage());
                            }

                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response.message());
                        iHomeView.onThrowMessage(response.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BannerResponse> call, @NonNull Throwable t) {
                    iHomeView.onThrowMessage(t.getMessage() + "getListBanner");
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getListBanner: " + e.getMessage());
            iHomeView.onThrowMessage(e.getMessage());
        }
    }

    public void getListCategory() {
        try {
            getCategory = apiService.getCategory(token, customerID);
            getCategory.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: " + code);
                            List<Category> data = response.body().getCategories();
                            iHomeView.onListCategory(data);
                        } else if (statusCode == 400) {
                            if (code.equals("auth/wrong-token")) {
                                iHomeView.onFinish();
                            } else {
                                Log.w(TAG, "onResponse400: " + code);
                                iHomeView.onThrowMessage(response.body().getMessage());
                            }
                        }
                    } else {
                        Log.w(TAG, "onResponse: getListCategory: " + response);
                        iHomeView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                    iHomeView.onThrowMessage(t.getMessage() + "getListCategory");
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getListCategory: " + e.getMessage());
            iHomeView.onThrowMessage(e.getMessage());
        }
    }

    public void getListSellingProduct() {
        try {
            getSellingProduct = apiService.getSellingProduct(token, customerID);
            getSellingProduct.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: " + code);
                            List<Product> data = response.body().getProducts();
                            iHomeView.onListSellingProduct(data);
                        } else if (statusCode == 400) {
                            if (code.equals("auth/wrong-token")) {
                                iHomeView.onFinish();
                            } else {
                                Log.w(TAG, "onResponse400: " + code);
                                iHomeView.onThrowMessage(response.body().getMessage());
                            }
                        }
                    } else {
                        Log.w(TAG, "onResponse: getListSellingProduct: " + response);
                        iHomeView.onThrowMessage("body null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                    iHomeView.onThrowMessage(t.getMessage() + "getListSellingProduct");
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "getListSellingProduct: " + e.getMessage());
            iHomeView.onThrowMessage(e.getMessage());
        }
    }

}
