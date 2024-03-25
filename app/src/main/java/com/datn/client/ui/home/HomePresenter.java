package com.datn.client.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Product;
import com.datn.client.response.BannerResponse;
import com.datn.client.response.CategoryResponse;
import com.datn.client.response.ProductResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.BasePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter extends BasePresenter {
    private final FragmentActivity context;

    private final IHomeView iHomeView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<BannerResponse> getBanner;
    private Call<CategoryResponse> getCategory;
    private Call<ProductResponse> getSellingProduct;
    private Call<ProductResponse> searchProduct;

    public HomePresenter(FragmentActivity context, IHomeView iHomeView, ApiService apiService, String token, String customerID) {
        super(context, iHomeView, apiService, token, customerID);
        this.context = context;
        this.iHomeView = iHomeView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void cancelAPI() {
        super.cancelAPI();
        if (getBanner != null) {
            getBanner.cancel();
        }
        if (getCategory != null) {
            getCategory.cancel();
        }
        if (getSellingProduct != null) {
            getSellingProduct.cancel();
        }
        if (searchProduct != null) {
            searchProduct.cancel();
        }
    }

    public void getListBanner() {
        context.runOnUiThread(() -> {
            try {
                getBanner = apiService.getBanner(token, customerID);
                getBanner.enqueue(new Callback<BannerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BannerResponse> call, @NonNull Response<BannerResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iHomeView.onThrowLog("getListBanner200", code);
                                List<Banner> data = response.body().getBanners();
                                iHomeView.onListBanner(data);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iHomeView.onFinish();
                                } else {
                                    iHomeView.onThrowLog("getListBanner400", code);
                                    iHomeView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iHomeView.onThrowLog("getListBanner: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BannerResponse> call, @NonNull Throwable t) {
                        iHomeView.onThrowLog("getListBanner: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iHomeView.onThrowLog("getListBanner", e.getMessage());
            }
        });
    }

    public void getListCategory() {
        context.runOnUiThread(() -> {
            try {
                getCategory = apiService.getCategory(token, customerID);
                getCategory.enqueue(new Callback<CategoryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iHomeView.onThrowLog("getListCategory200", code);
                                List<Category> data = response.body().getCategories();
                                iHomeView.onListCategory(data);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iHomeView.onFinish();
                                } else {
                                    iHomeView.onThrowLog("getListCategory400", code);
                                    iHomeView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iHomeView.onThrowLog("getListCategory: onResponse", response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                        iHomeView.onThrowLog("getListCategory: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iHomeView.onThrowLog("getListCategory", e.getMessage());
            }
        });
    }

    public void getListSellingProduct() {
        context.runOnUiThread(() -> {
            try {
                getSellingProduct = apiService.getSellingProduct(token, customerID);
                getSellingProduct.enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iHomeView.onThrowLog("getListSellingProduct200", code);
                                List<Product> data = response.body().getProducts();
                                iHomeView.onListSellingProduct(data);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iHomeView.onFinish();
                                } else {
                                    iHomeView.onThrowLog("getListSellingProduct400", code);
                                    iHomeView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iHomeView.onThrowLog("getListSellingProduct: onResponse", response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                        iHomeView.onThrowLog("getListSellingProduct: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iHomeView.onThrowLog("getListSellingProduct", e.getMessage());
            }
        });
    }

    public void searchProduct(String keyword) {
        context.runOnUiThread(() -> {
            try {
                searchProduct = apiService.searchProduct(token, customerID, keyword);
                searchProduct.enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iHomeView.onThrowLog("searchProduct200", code);
                                List<Product> data = response.body().getProducts();
//                                Log.w("searchProduct", "onResponse: " + data.toString());
                                iHomeView.onSearchProduct(data);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iHomeView.onFinish();
                                } else {
                                    iHomeView.onThrowLog("searchProduct400", code);
                                    iHomeView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iHomeView.onThrowLog("searchProduct: onResponse", response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                        iHomeView.onThrowLog("searchProduct: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iHomeView.onThrowLog("searchProduct", e.getMessage());
            }
        });
    }
}
