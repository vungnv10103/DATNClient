package com.datn.client.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.datn.client.adapter.BannerAdapter;
import com.datn.client.adapter.CategoryAdapter;
import com.datn.client.adapter.ProductAdapter;
import com.datn.client.databinding.FragmentHomeBinding;
import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.models.Customer;
import com.datn.client.models.Product;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.LoginActivity;
import com.datn.client.ui.MyDialog;
import com.datn.client.ui.product.DetailProductActivity;
import com.datn.client.ui.product.ListProductActivity;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment implements IHomeView {

    private FragmentHomeBinding binding;
    private final Handler handler = new Handler();
    private HomePresenter homePresenter;
    private PreferenceManager preferenceManager;


    private SpinKitView spinKitBanner, spinKitCate, spinKitSellingProduct;
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

        homePresenter.getListBanner();
        homePresenter.getListCategory();
        homePresenter.getListSellingProduct();


    }

    private void displayBanner() {
        if (mBannerList.size() == 0) {
            showToast("No banner");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            BannerAdapter adapterSlideShow = new BannerAdapter(getContext(), mBannerList, banner -> showToast(banner.get_id()));
            vpgBanner.setAdapter(adapterSlideShow);
            animationSlideShow();
        });

    }

    private void displayCategory() {
        if (mCategoryList.size() == 0) {
            showToast("No category");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            if (mCategoryList.size() > 12) {
                if (!mCategoryList.get(11).getName().equals("Xem thêm")) {
                    String temp = "https://cdn-icons-png.flaticon.com/512/10348/10348994.png";
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
        if (mProductList.size() == 0) {
            showToast("No product");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            ProductAdapter productAdapter = new ProductAdapter(getActivity(), mProductList, product -> {
                Intent intent = new Intent(requireActivity(), DetailProductActivity.class);
                intent.putExtra("productID", product.get_id());
                startActivity(intent);
            });
            rcvSellingProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rcvSellingProduct.setAdapter(productAdapter);
        });
    }

    private void onBannerLoaded() {
        displayBanner();
        spinKitBanner.setVisibility(View.GONE);
        layoutBanner.setVisibility(View.VISIBLE);
    }

    private void onCategoryLoaded() {
        displayCategory();
        spinKitCate.setVisibility(View.GONE);
        rcvCategory.setVisibility(View.VISIBLE);
    }

    private void onSellingProductLoaded() {
        displaySellingProduct();
        spinKitSellingProduct.setVisibility(View.GONE);
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

    public void switchToLogin() {
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }

    @Override
    public void onThrowMessage(@NonNull String message) {
        MyDialog.gI().startDlgOK(requireActivity(), message);
    }

    @Override
    public void onFinish() {
        switchToLogin();
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            requireActivity().finishAffinity();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            requireActivity().finishAffinity();
        }
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }


    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        homePresenter = new HomePresenter(this, apiService, mToken, mCustomer.get_id());
    }

    private void initUI() {
        layoutBanner = binding.layoutBanner;
        vpgBanner = binding.vpgSlideshow;
        indicatorBanner = binding.indicator;
        rcvCategory = binding.rcvCategories;
        rcvSellingProduct = binding.rcvProduct;
        spinKitBanner = binding.spinKitBanner;
        spinKitCate = binding.spinKitCate;
        spinKitSellingProduct = binding.spinKitSellPro;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        switchToLogin();
    }
}