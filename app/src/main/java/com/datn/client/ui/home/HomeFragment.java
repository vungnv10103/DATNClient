package com.datn.client.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.datn.client.action.IAction;
import com.datn.client.adapter.BannerAdapter;
import com.datn.client.adapter.CategoryAdapter;
import com.datn.client.databinding.FragmentHomeBinding;
import com.datn.client.models.Banner;
import com.datn.client.models.BaseModel;
import com.datn.client.models.Category;
import com.datn.client.models.Customer;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment implements IHomeView {

    private FragmentHomeBinding binding;
    private final Handler handler = new Handler();
    private HomePresenter homePresenter;
    private PreferenceManager preferenceManager;

    private ViewPager2 vpgBanner;
    private CircleIndicator3 indicatorBanner;

    private Customer mCustomer;
    private String mToken;

    private boolean bannerLoaded = false;
    private List<Banner> mBannerList;
    public static long delayBanner = 3000;

    public static boolean isDisableItemCate = true;
    private RecyclerView rcvCategory;
    private List<Category> mCategoryList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initUI();
        initService();
        checkLogin();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homePresenter.getListBanner(mToken, mCustomer.get_id());
        homePresenter.getListCategory(mToken, mCustomer.get_id());
    }

    @Override
    public void onListBanner(List<Banner> bannerList) {
        this.mBannerList = bannerList;
        if (bannerList.size() == 0) {
            return;
        }
        bannerLoaded = true;
        BannerAdapter adapterSlideShow = new BannerAdapter(getContext(), bannerList, banner -> {
            showToast(banner.get_id());
        });
        vpgBanner.setAdapter(adapterSlideShow);
        animationSlideShow();
    }

    @Override
    public void onListCategory(List<Category> categoryList) {
        this.mCategoryList = categoryList;
        displayCategory();
    }

    @Override
    public void onThrowMessage(String message) {
        MyDialog.gI().startDlgOK(requireActivity(), message);
    }

    private void displayCategory() {
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
            CategoryAdapter categoriesAdapter = new CategoryAdapter(getActivity(), mCategoryList, (category) -> {
                showToast(category.getCreated_at());

            });
            rcvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            rcvCategory.setAdapter(categoriesAdapter);
        });
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
            return;
        }
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }


    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        homePresenter = new HomePresenter(this, apiService);
    }

    private void initUI() {
        vpgBanner = binding.vpgSlideshow;
        indicatorBanner = binding.indicator;
        rcvCategory = binding.rcvCategories;
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
        if (bannerLoaded) {
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
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}