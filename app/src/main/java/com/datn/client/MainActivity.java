package com.datn.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.datn.client.databinding.ActivityMainBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.ui.home.HomeFragment;
import com.datn.client.ui.notifications.NotificationsFragment;
import com.datn.client.utils.ManagerUser;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements IBaseView {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BasePresenter basePresenter;
    private Customer mCustomer;
    private String mToken;

    public NavController navController;
    private NavHostFragment navHostFragment;
    public Fragment currentFragment;
    private TextView cartBadgeTextView;
    private boolean isExit = false;
    private static final long DELAY = 3000;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (navHostFragment != null) {
                currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (currentFragment instanceof HomeFragment) {
                    if (isExit) {
                        doExit();
                        return;
                    }
                    isExit = true;
                    showToast(getString(R.string.press_back_again_to_exit));
                    new Handler().postDelayed(() -> isExit = false, DELAY);
                } else if (currentFragment instanceof NotificationsFragment) {
                    if (NotificationsFragment.isLayoutActionShow) {
                        NotificationsFragment.resetAction();
                    } else {
                        navController.popBackStack();
                    }
                } else {
                    navController.popBackStack();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        mCustomer = ManagerUser.gI().getCustomerLogin(this);
        mToken = ManagerUser.gI().checkToken(this);
        if (mCustomer == null || mToken == null) {
            reLogin();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_notifications,
                R.id.navigation_dashboard)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        @SuppressLint("RestrictedApi")
        BottomNavigationMenuView mBottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0);
        View view = mBottomNavigationMenuView.getChildAt(2);

        @SuppressLint("RestrictedApi")
        BottomNavigationItemView itemView = (BottomNavigationItemView) view;
        View cart_badge = LayoutInflater.from(this).inflate(R.layout.badge_layout, mBottomNavigationMenuView, false);
        itemView.addView(cart_badge);
        cartBadgeTextView = cart_badge.findViewById(R.id.cart_badge);
        cartBadgeTextView.setVisibility(View.VISIBLE);
        cartBadgeTextView.setText("0");

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) cartBadgeTextView.getLayoutParams();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                String fragmentName = destination.getLabel().toString();
                int marginTopInDp = 15;
                if (fragmentName.equals(getString(R.string.title_notifications))) {
                    marginTopInDp = 5;
                }
                int marginTopInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginTopInDp, getResources().getDisplayMetrics());
                layoutParams.setMargins(0, marginTopInPx, 0, 0);
                cartBadgeTextView.setLayoutParams(layoutParams);
                Log.d("FragmentName", fragmentName);
            } else {
                Log.d("FragmentName", "Label null");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        initService();
        basePresenter.getNotification();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a keyboard is available
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            MyDialog.gI().startDlgOK(this, "Keyboard available");
        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            MyDialog.gI().startDlgOK(this, "No keyboard");
        }

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                MyDialog.gI().startDlgOK(this, "Night mode is not active, we're using the light theme");
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                MyDialog.gI().startDlgOK(this, "Night mode is active, we're using dark theme");
                break;
        }
    }

    @Override
    public void onListNotification(@NonNull List<Notification> notificationList) {
        cartBadgeTextView.setText(String.valueOf(notificationList.size()));
    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(MainActivity.this, overlayMessages, basePresenter);
    }
    @Override
    public void onThrowNotification(String notification) {
        MyDialog.gI().startDlgOK(this, notification);
    }

    @Override
    public void onThrowMessage(@NonNull MessageDetailResponse message) {
        switch (message.getCode()) {
            case "overlay/update-status-success":
            case "notification/update-status-success":
                showLogW(message.getTitle(), message.getContent());
                break;
            default:
                MyDialog.gI().startDlgOK(MainActivity.this, message.getContent());
                break;
        }
    }

    @Override
    public void onThrowLog(String key, String message) {
        showLogW(key, message);
    }

    private void doExit() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }

    private void showToast(@NonNull Object message) {
        Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        finishAffinity();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        basePresenter = new BasePresenter(MainActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    @Override
    public void onFinish() {
        doExit();
    }
}
