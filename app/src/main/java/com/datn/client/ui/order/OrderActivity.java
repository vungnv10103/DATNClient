package com.datn.client.ui.order;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.client.R;
import com.datn.client.activity.ViewPagerOrderAdapter;
import com.datn.client.databinding.ActivityOrderBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class OrderActivity extends AppCompatActivity {
    private ActivityOrderBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 vpgOrder;

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

        initUI();
        ViewPagerOrderAdapter viewPagerOrderAdapter = new ViewPagerOrderAdapter(OrderActivity.this);
        vpgOrder.setAdapter(viewPagerOrderAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, vpgOrder, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.logo_app_gradient);
                    tab.setText(R.string.order_waiting_confirm);
                    BadgeDrawable badge = tab.getOrCreateBadge();
                    badge.setNumber(99);
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

    @Override
    protected void onStart() {
        super.onStart();

        initEventClick();
    }

    private void initEventClick() {

    }


    @SuppressLint("ResourceType")
    private void initUI() {
        tabLayout = binding.tabLayout;
        vpgOrder = binding.viewPagerOrder;
    }

}
