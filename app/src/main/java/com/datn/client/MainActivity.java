package com.datn.client;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.datn.client.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private boolean isExit = false;
    private static final long DELAY = 3000;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            if (navHostFragment != null) {
                Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (currentFragment instanceof HomeFragment) {
                    if (isExit) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return;
                    }
                    isExit = true;
                    Toast.makeText(MainActivity.this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> isExit = false, DELAY);
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
        final TextView cartBadgeTextView = cart_badge.findViewById(R.id.cart_badge);
        cartBadgeTextView.setVisibility(View.VISIBLE);
        cartBadgeTextView.setText("2");

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) cartBadgeTextView.getLayoutParams();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                String fragmentName = destination.getLabel().toString();
                int marginTopInDp = 15;
                if (fragmentName.equals("Notifications")) {
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
}
