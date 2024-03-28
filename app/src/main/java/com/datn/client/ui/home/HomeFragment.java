package com.datn.client.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.adapter.BannerAdapter;
import com.datn.client.adapter.CategoryAdapter;
import com.datn.client.adapter.ProductAdapter;
import com.datn.client.databinding.FragmentHomeBinding;
import com.datn.client.models.Banner;
import com.datn.client.models.Category;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models.Product;
import com.datn.client.models._BaseModel;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.ui.product.DetailProductActivity;
import com.datn.client.ui.product.ListProductActivity;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.List;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment implements IHomeView {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding binding;
    private final Handler handler = new Handler();
    private HomePresenter homePresenter;
    private PreferenceManager preferenceManager;

    private CircularProgressIndicator progressBarLoading, progressBarBanner, progressBarCate, progressBarSellingProduct;
    private LinearLayout layoutHome;
    private RelativeLayout layoutBanner;
    private ViewPager2 vpgBanner;
    private CircleIndicator3 indicatorBanner;

    private Customer mCustomer;
    private String mToken;

    private List<Banner> mBannerList;
    public static long delayBanner = 3000;

    public static boolean isDisableItemCate = true;
    private RecyclerView rcvCategory, rcvSellingProduct, rcvSearchProduct;
    private List<Category> mCategoryList;
    private List<Product> mProductList, mProductSearch;

    private SearchBar searchBarProduct;
    private SearchView searchViewProduct;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initUI();
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().checkCustomer(requireActivity());
        mToken = ManagerUser.gI().checkToken(requireActivity());
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventClick();
        initService();
    }

    @Override
    public void onStart() {
        super.onStart();

        homePresenter.getListBanner();
        homePresenter.getListCategory();
        homePresenter.getListSellingProduct();
        homePresenter.getOverlayMessage();
    }

    private void displayBanner() {
        if (mBannerList.isEmpty()) {
            showToast("No banner");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            BannerAdapter adapterSlideShow = new BannerAdapter(getContext(), mBannerList, new IAction() {
                @Override
                public void onClick(_BaseModel banner) {

                }

                @Override
                public void onLongClick(_BaseModel banner) {

                }

                @Override
                public void onItemClick(_BaseModel banner) {

                }
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

            CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), mCategoryList, new IAction() {
                @Override
                public void onClick(_BaseModel category) {
                    Intent intent = new Intent(requireActivity(), ListProductActivity.class);
                    intent.putExtra("categoryID", category.get_id());
                    startActivity(intent);
                }

                @Override
                public void onLongClick(_BaseModel category) {

                }

                @Override
                public void onItemClick(_BaseModel category) {

                }
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
            ProductAdapter productSellingAdapter = new ProductAdapter(getActivity(), mProductList,
                    R.layout.item_product, new IAction() {
                @Override
                public void onClick(_BaseModel product) {
                    Intent intent = new Intent(requireActivity(), DetailProductActivity.class);
                    intent.putExtra("productID", product.get_id());
                    startActivity(intent);
                }

                @Override
                public void onLongClick(_BaseModel product) {

                }

                @Override
                public void onItemClick(_BaseModel product) {

                }
            });
            GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
            rcvSellingProduct.setLayoutManager(glm);
            rcvSellingProduct.setAdapter(productSellingAdapter);
        });
    }

    private void displaySearchProduct(boolean isAutoShowKeyBoard) {
        if (mProductSearch.isEmpty()) {
            showToast("No product");
            return;
        }
        requireActivity().runOnUiThread(() -> {
            ProductAdapter productSearchAdapter = new ProductAdapter(getActivity(), mProductSearch,
                    R.layout.item_product_search, new IAction() {
                @Override
                public void onClick(_BaseModel product) {
                    Intent intent = new Intent(requireActivity(), DetailProductActivity.class);
                    intent.putExtra("productID", product.get_id());
                    startActivity(intent);
                }

                @Override
                public void onLongClick(_BaseModel product) {

                }

                @Override
                public void onItemClick(_BaseModel product) {

                }
            });
            LinearLayoutManager llm = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
            rcvSearchProduct.setLayoutManager(llm);
            rcvSearchProduct.setAdapter(productSearchAdapter);
            setLoading(false);
            searchViewProduct.setAutoShowKeyboard(isAutoShowKeyBoard);
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
        this.mProductSearch = productList;
        onSellingProductLoaded();
        displaySearchProduct(true);
    }

    @Override
    public void onSearchProduct(List<Product> productList) {
        this.mProductSearch = productList;
        displaySearchProduct(false);
        searchViewProduct.show();
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {

    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(requireActivity(), overlayMessages, homePresenter);
    }

    @Override
    public void onThrowMessage(@NonNull MessageResponse message) {
        switch (message.getCode()) {
            case "overlay/update-status-success":
            case "notification/update-status-success":
                showLogW(message.getTitle(), message.getContent());
                break;
            default:
                MyDialog.gI().startDlgOK(requireActivity(), message.getContent());
                break;
        }
    }

    @Override
    public void onThrowLog(String key, String message) {
        showLogW(key, message);
    }


    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }


    @Override
    public void onFinish() {
        reLogin();
    }


    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        homePresenter = new HomePresenter(requireActivity(), this, apiService, mToken, mCustomer.get_id());
    }

    private void initEventClick() {
        searchBarProduct.inflateMenu(R.menu.searchbar_menu);
        searchBarProduct.setOnMenuItemClickListener(
                menuItem -> {
                    // Handle menuItem click.
                    int idItem = menuItem.getItemId();
                    if (idItem == R.id.menu_account) {
                        MyDialog.gI().startDlgOK(requireActivity(), Objects.requireNonNull(menuItem.getTitle()).toString());
                    } else if (idItem == R.id.menu_cast) {
                        MyDialog.gI().startDlgOK(requireActivity(), Objects.requireNonNull(menuItem.getTitle()).toString());
                    }
                    return true;
                });
        searchViewProduct
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            if (event != null) {
                                System.out.println(event);
                                switch (event.getKeyCode()) {
                                    case KeyEvent.KEYCODE_ENTER:
                                        String keyword = searchViewProduct.getText().toString();
                                        Log.d(TAG, "KEYCODE_ENTER: " + keyword);
                                        break;
                                    case KeyEvent.KEYCODE_BACKSLASH:
                                        Log.d(TAG, "KEYCODE_BACKSLASH: " + searchViewProduct.getText());
                                        break;
                                }
                            }
                            String keyword = searchViewProduct.getText().toString();
                            setLoading(true);
                            homePresenter.searchProduct(keyword);
                            searchBarProduct.setText(keyword);
                            searchViewProduct.hide();
                            return false;
                        });
        searchViewProduct.setupWithSearchBar(searchBarProduct);
    }

    private void setLoading(boolean isLoading) {
        layoutHome.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void initUI() {
        layoutHome = binding.layoutHome;
        layoutBanner = binding.layoutBanner;
        vpgBanner = binding.vpgSlideshow;
        indicatorBanner = binding.indicator;
        rcvCategory = binding.rcvCategories;
        rcvSellingProduct = binding.rcvProduct;
        rcvSearchProduct = binding.rcvProductSearch;
        progressBarLoading = binding.progressbarLoading;
        progressBarBanner = binding.progressbarBanner;
        progressBarCate = binding.progressbarCategory;
        progressBarSellingProduct = binding.progressbarProduct;
        searchBarProduct = binding.searchBar;
        searchViewProduct = binding.searchViewProduct;
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
        homePresenter.onCancelAPI();
    }

}