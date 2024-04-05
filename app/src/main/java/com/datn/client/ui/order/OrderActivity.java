package com.datn.client.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.client.R;
import com.datn.client.adapter.ViewPagerOrderAdapter;
import com.datn.client.databinding.ActivityOrderBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OrdersDetail;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models.Product;
import com.datn.client.models.ProductOrder;
import com.datn.client.models.ProductOrderDetail;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.animation.DepthPageTransformer;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;
import com.datn.client.utils.STATUS_ORDER;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity implements IOrderView {
    private static final String TAG = OrderActivity.class.getSimpleName();
    public static final int POSITION_WAITING_TAB = 0;
    public static final int POSITION_PREPARE_TAB = 1;
    public static final int POSITION_IN_TRANSIT_TAB = 2;
    public static final int POSITION_PAID_TAB = 3;
    public static final int POSITION_CANCEL_TAB = 4;
    private ActivityOrderBinding binding;
    private OrderPresenter orderPresenter;
    private PreferenceManager preferenceManager;
    private CircularProgressIndicator progressViewPager2;
    private static TabLayout tabLayout;
    private ViewPager2 vpgOrder;

    private Customer mCustomer;
    private String mToken;
    private static OrdersDetail mOrdersDetail;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            int tabSelectedPos = tabLayout.getSelectedTabPosition();
            if (tabSelectedPos == 0) {
                tabLayout.clearOnTabSelectedListeners();
                finish();
            } else {
                tabLayout.selectTab(tabLayout.getTabAt(tabSelectedPos - 1));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.order), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();
        checkRequire();
        initService();
    }

    private void checkRequire() {
        preferenceManager = new PreferenceManager(OrderActivity.this, Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().checkCustomer(this);
        mToken = ManagerUser.gI().checkToken(this);
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLoading(true);
        initViewPager();
    }

    @NonNull
    public static List<ProductOrderDetail> getProductOrderDetail(@NonNull List<ProductOrder> dataProductOrder) {
        List<ProductOrderDetail> dataProductOrderDetail = new ArrayList<>();
        for (int i = 0; i < dataProductOrder.size(); i++) {
            ProductOrder productOrder = dataProductOrder.get(i);
            String orderID = productOrder.getOrder_id();
            List<Product> products = productOrder.getProducts();
            List<String> productsQuantity = productOrder.getProductsQuantity();
            List<String> orderDetailID = productOrder.getOrderDetailID();
            List<Integer> mOrderDetailStatus = productOrder.getOrderDetailStatus();
            String amount = productOrder.getAmount();
            for (int j = 0; j < products.size(); j++) {
                ProductOrderDetail productOrderDetail = new ProductOrderDetail();
                productOrderDetail.setOrder_id(orderID);
                productOrderDetail.setOrder_detail_id(orderDetailID.get(j));
                productOrderDetail.setProduct_id(products.get(j).get_id());
                productOrderDetail.setProduct_image(products.get(j).getImg_cover());
                productOrderDetail.setProduct_name(products.get(j).getName());
                productOrderDetail.setPrice(amount);
                productOrderDetail.setQuantity(Integer.parseInt(productsQuantity.get(j)));
                productOrderDetail.setStatus(mOrderDetailStatus.get(j));
                dataProductOrderDetail.add(productOrderDetail);
            }
        }
        return dataProductOrderDetail;
    }


    public static void displayCustomBadge(int positionTab, int number, @DrawableRes int resId) {
        TabLayout.Tab tab = tabLayout.getTabAt(positionTab);
        if (tab != null) {
            if (number > 0) {
                BadgeDrawable badge = tab.getOrCreateBadge();
                badge.setNumber(number);
            }
            if (resId == -1) {
                return;
            }
            tab.setIcon(resId);
        }
    }

    public static List<ProductOrder> get(int status) {
        if (status == STATUS_ORDER.WAIT_CONFIRM.getValue()) {
            return mOrdersDetail.getWaitingList();
        } else if (status == STATUS_ORDER.PREPARE.getValue()) {
            return mOrdersDetail.getPrepareList();
        } else if (status == STATUS_ORDER.IN_TRANSIT.getValue()) {
            return mOrdersDetail.getInTransitList();
        } else if (status == STATUS_ORDER.PAID.getValue()) {
            return mOrdersDetail.getPaidList();
        } else if (status == STATUS_ORDER.CANCEL.getValue()) {
            return mOrdersDetail.getCancelList();
        } else {
            return new ArrayList<>();
        }
    }


    @Override
    public void onLoadOrders(OrdersDetail orderStatus) {
        if (orderStatus != null) {
            mOrdersDetail = orderStatus;
        } else {
            showToast("no data");
        }
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {
        Log.d(TAG, "onListNotification: " + notificationList);
    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(OrderActivity.this, overlayMessages, orderPresenter);
    }

    @Override
    public void onThrowMessage(MessageResponse message) {
        if (message != null) {
            switch (message.getCode()) {
                case "overlay/update-status-success":
                case "notification/update-status-success":
                    showLogW(message.getTitle(), message.getContent());
                    break;
                default:
                    MyDialog.gI().startDlgOK(OrderActivity.this, message.getContent());
                    break;
            }
        }
    }

    private void setLoading(boolean isLoading) {
        vpgOrder.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        progressViewPager2.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onThrowLog(String key, String message) {
        showLogW(key, message);
        setLoading(false);
    }

    @Override
    public void onFinish() {
        reLogin();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        orderPresenter = new OrderPresenter(OrderActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private void initViewPager() {
        ViewPagerOrderAdapter viewPagerOrderAdapter = new ViewPagerOrderAdapter(OrderActivity.this);
        vpgOrder.setAdapter(viewPagerOrderAdapter);
//        vpgOrder.setPageTransformer(new ZoomOutPageTransformer());
        vpgOrder.setPageTransformer(new DepthPageTransformer());
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, vpgOrder, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.order_waiting_confirm);
                    break;
                case 1:
                    tab.setText(R.string.prepare);
                    break;
                case 2:
                    tab.setText(R.string.order_in_transit);
                    break;
                case 3:
                    tab.setText(R.string.order_paid);
                    break;
                case 4:
                    tab.setText(R.string.order_cancel);
                    break;
            }
        });
        tabLayoutMediator.attach();
        setLoading(false);
    }

    private void initUI() {
        tabLayout = binding.tabLayout;
        vpgOrder = binding.viewPagerOrder;
        progressViewPager2 = binding.progressbarViewpager2;
    }

    private void showToast(String message) {
        Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private static void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        orderPresenter.onCancelAPI();
    }
}
