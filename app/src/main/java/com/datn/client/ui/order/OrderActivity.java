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
import com.datn.client.activity.ViewPagerOrderAdapter;
import com.datn.client.databinding.ActivityOrderBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OrdersDetail;
import com.datn.client.models.OverlayMessage;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.animation.DepthPageTransformer;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity implements IOrderView {
    private static final String TAG = OrderActivity.class.getSimpleName();
    private static final int POSITION_WAITING_TAB = 0;
    private static final int POSITION_PREPARE_TAB = 1;
    private static final int POSITION_IN_TRANSIT_TAB = 2;
    private static final int POSITION_PAID_TAB = 3;
    private static final int POSITION_CANCEL_TAB = 4;
    private ActivityOrderBinding binding;
    private OrderPresenter orderPresenter;
    private PreferenceManager preferenceManager;
    private CircularProgressIndicator progressViewPager2;
    private TabLayout tabLayout;
    private ViewPager2 vpgOrder;

    private Customer mCustomer;
    private String mToken;
    private OrdersDetail mOrdersDetail;

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
        preferenceManager = new PreferenceManager(OrderActivity.this, Constants.KEY_PREFERENCE_ACC);

        checkLogin();
        initService();
    }

    @Override
    protected void onStart() {
        super.onStart();

        orderPresenter.getAllOrders();

    }

    private void displayCustomBadge(TabLayout.Tab tab, int number, @DrawableRes int resId) {
        if (tab != null) {
            BadgeDrawable badge = tab.getOrCreateBadge();
            badge.setNumber(number);
            if (resId == -1) {
                return;
            }
            tab.setIcon(resId);
        }
    }

    private void displayOrders() {
        if (mOrdersDetail.getWaitingList() != null) {
            displayCustomBadge(tabLayout.getTabAt(POSITION_WAITING_TAB), mOrdersDetail.getWaitingList().size(), R.drawable.logo_app_gradient);
        }
        if (mOrdersDetail.getPrepareList() != null) {
            displayCustomBadge(tabLayout.getTabAt(POSITION_PREPARE_TAB), mOrdersDetail.getPrepareList().size(), -1);
        }
        if (mOrdersDetail.getInTransitList() != null) {
            displayCustomBadge(tabLayout.getTabAt(POSITION_IN_TRANSIT_TAB), mOrdersDetail.getInTransitList().size(), -1);
        }
        if (mOrdersDetail.getPaidList() != null) {
            displayCustomBadge(tabLayout.getTabAt(POSITION_PAID_TAB), mOrdersDetail.getPaidList().size(), -1);
        }
        if (mOrdersDetail.getCancelList() != null) {
            displayCustomBadge(tabLayout.getTabAt(POSITION_CANCEL_TAB), mOrdersDetail.getCancelList().size(), -1);
        }
        vpgOrder.setVisibility(View.VISIBLE);
        progressViewPager2.setVisibility(View.GONE);
    }


    @Override
    public void onLoadOrders(OrdersDetail orderStatus) {
        if (orderStatus != null) {
            this.mOrdersDetail = orderStatus;
            initViewPager();
            displayOrders();
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
    public void onThrowMessage(@NonNull MessageResponse message) {
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

    @Override
    public void onThrowLog(String key, String message) {
        showLogW(key, message);
    }

    @Override
    public void onFinish() {
        switchToLogin();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        orderPresenter = new OrderPresenter(OrderActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private void initViewPager() {
        ViewPagerOrderAdapter viewPagerOrderAdapter = new ViewPagerOrderAdapter(OrderActivity.this, mOrdersDetail);
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
    }

    private void initUI() {
        tabLayout = binding.tabLayout;
        vpgOrder = binding.viewPagerOrder;
        progressViewPager2 = binding.progressbarViewpager2;
    }

    private void showToast(String message) {
        Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        finishAffinity();
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
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

    public void switchToLogin() {
        preferenceManager.clear();
        startActivity(new Intent(OrderActivity.this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        orderPresenter.onCancelAPI();
    }
}
