package com.datn.client.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.datn.client.ui.order.CancelOrderFragment;
import com.datn.client.ui.order.InTransitOrderFragment;
import com.datn.client.ui.order.PrepareOrderFragment;
import com.datn.client.ui.order.PaidOrderFragment;
import com.datn.client.ui.order.WaitConfirmFragment;

public class ViewPagerOrderAdapter extends FragmentStateAdapter {
    public static final int NUMBER_TAB = 5;

    public ViewPagerOrderAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = WaitConfirmFragment.newInstance();
        switch (position) {
            case 0:
                fragment = WaitConfirmFragment.newInstance();
                break;
            case 1:
                fragment = PrepareOrderFragment.newInstance();
                break;
            case 2:
                fragment = InTransitOrderFragment.newInstance();
                break;
            case 3:
                fragment = PaidOrderFragment.newInstance();
                break;
            case 4:
                fragment = CancelOrderFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUMBER_TAB;
    }
}
