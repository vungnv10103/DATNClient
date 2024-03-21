package com.datn.client.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.client.R;
import com.datn.client.adapter.BannerAdapter;
import com.datn.client.adapter.CategoryAdapter;
import com.datn.client.adapter.ProductAdapter;
import com.datn.client.databinding.FragmentHomeBinding;
import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Product;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.product.DetailProductActivity;
import com.datn.client.ui.product.ListProductActivity;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment implements IHomeView {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentHomeBinding binding;
    private final Handler handler = new Handler();
    private HomePresenter homePresenter;
    private PreferenceManager preferenceManager;


    private CircularProgressIndicator progressBarBanner, progressBarCate, progressBarSellingProduct;
    private RelativeLayout layoutBanner;
    private ViewPager2 vpgBanner;
    private CircleIndicator3 indicatorBanner;

    private Customer mCustomer;
    private String mToken;

    private List<Banner> mBannerList;
    public static long delayBanner = 3000;

    public static boolean isDisableItemCate = true;
    private RecyclerView rcvCategory, rcvSellingProduct;
    private List<Category> mCategoryList;
    private List<Product> mProductList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initUI();
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        checkLogin();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initService();
    }

    @Override
    public void onStart() {
        super.onStart();

        homePresenter.getListBanner();
        homePresenter.getListCategory();
        homePresenter.getListSellingProduct();
    }

    private void displayBanner() {
        if (mBannerList.isEmpty()) {
            showToast("No banner");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            BannerAdapter adapterSlideShow = new BannerAdapter(getContext(), mBannerList, banner -> {

            });
            vpgBanner.setAdapter(adapterSlideShow);
            animationSlideShow();
        });
    }

    private void displayCategory() {
        if (mCategoryList.isEmpty()) {
            showToast("No category");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            // limit display category
            if (mCategoryList.size() > 12) {
                if (!mCategoryList.get(11).getName().equals("Xem thêm")) {
                    String temp = "https://stech-993p.onrender.com/images/category-more.png";
                    Category viewMore = new Category("-1", "Xem thêm", "---", temp);
                    Category viewLess = new Category("-1", "Ẩn bớt", "---", temp);
                    if (isDisableItemCate) {
                        mCategoryList.add(11, viewMore);
                    } else {
                        mCategoryList.add(mCategoryList.size(), viewLess);
                    }
                }
            }

            CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), mCategoryList, category -> {
                Intent intent = new Intent(requireActivity(), ListProductActivity.class);
                intent.putExtra("categoryID", category.get_id());
                startActivity(intent);
            });
            rcvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            rcvCategory.setAdapter(categoryAdapter);
        });
    }

    private void displaySellingProduct() {
        if (mProductList.isEmpty()) {
            showToast("No product");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            ProductAdapter productAdapter = new ProductAdapter(getActivity(), mProductList, product -> {
                Intent intent = new Intent(requireActivity(), DetailProductActivity.class);
                intent.putExtra("productID", product.get_id());
                startActivity(intent);
            });
            GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
            rcvSellingProduct.setLayoutManager(llm);
            rcvSellingProduct.setAdapter(productAdapter);
        });
    }

    private void onBannerLoaded() {
        displayBanner();
        progressBarBanner.setVisibility(View.GONE);
        layoutBanner.setVisibility(View.VISIBLE);
    }

    private void onCategoryLoaded() {
        displayCategory();
        progressBarCate.setVisibility(View.GONE);
        rcvCategory.setVisibility(View.VISIBLE);
    }

    private void onSellingProductLoaded() {
        displaySellingProduct();
        progressBarSellingProduct.setVisibility(View.GONE);
        rcvSellingProduct.setVisibility(View.VISIBLE);
    }


    @Override
    public void onListBanner(List<Banner> bannerList) {
        this.mBannerList = bannerList;
        onBannerLoaded();
    }

    @Override
    public void onListCategory(List<Category> categoryList) {
        this.mCategoryList = categoryList;
        onCategoryLoaded();
    }

    @Override
    public void onListSellingProduct(List<Product> productList) {
        this.mProductList = productList;
        onSellingProductLoaded();
    }

    @Override
    public void onThrowMessage(@NonNull MessageResponse message) {
        MyDialog.gI().startDlgOK(requireActivity(), message.getContent());
    }

    @Override
    public void onThrowLog(String key, String message) {
        showLogW(key, message);
    }

    public void switchToLogin() {
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        requireActivity().finishAffinity();
    }


    @Override
    public void onFinish() {
        switchToLogin();
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            reLogin();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            reLogin();
        }
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }


    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        homePresenter = new HomePresenter(requireActivity(), this, apiService, mToken, mCustomer.get_id());
    }

    private void initUI() {
        layoutBanner = binding.layoutBanner;
        vpgBanner = binding.vpgSlideshow;
        indicatorBanner = binding.indicator;
        rcvCategory = binding.rcvCategories;
        rcvSellingProduct = binding.rcvProduct;
        progressBarBanner = binding.progressbarBanner;
        progressBarCate = binding.progressbarCategory;
        progressBarSellingProduct = binding.progressbarProduct;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = vpgBanner.getCurrentItem();
            int slideCount = mBannerList.size();
            if (currentPosition == slideCount - 1) {
                vpgBanner.setCurrentItem(0);
            } else {
                vpgBanner.setCurrentItem(currentPosition + 1);
            }
        }
    };

    private void animationSlideShow() {
        // add animation to slide show
        vpgBanner.setClipToPadding(false);
        vpgBanner.setClipChildren(false);
        vpgBanner.setOffscreenPageLimit(3);
        vpgBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        vpgBanner.setPageTransformer(compositePageTransformer);
        indicatorBanner.setViewPager(vpgBanner);
        vpgBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayBanner);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        homePresenter.cancelAPI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        switchToLogin();
    }
}