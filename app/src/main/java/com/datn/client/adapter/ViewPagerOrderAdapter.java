package com.datn.client.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.datn.client.models.OrdersDetail;
import com.datn.client.ui.order.CancelOrderFragment;
import com.datn.client.ui.order.InTransitOrderFragment;
import com.datn.client.ui.order.PaidOrderFragment;
import com.datn.client.ui.order.PrepareOrderFragment;
import com.datn.client.ui.order.WaitConfirmFragment;

public class ViewPagerOrderAdapter extends FragmentStateAdapter {
    public static final int NUMBER_TAB = 5;
    private final OrdersDetail mOrderStatus;

    public ViewPagerOrderAdapter(@NonNull FragmentActivity fragmentActivity, OrdersDetail orderStatus) {
        super(fragmentActivity);
        this.mOrderStatus = orderStatus;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = WaitConfirmFragment.newInstance(mOrderStatus.getWaitingList());
        switch (position) {
            case 0:
                fragment = WaitConfirmFragment.newInstance(mOrderStatus.getWaitingList());
                break;
            case 1:
                fragment = PrepareOrderFragment.newInstance(mOrderStatus.getPrepareList());
                break;
            case 2:
                fragment = InTransitOrderFragment.newInstance(mOrderStatus.getInTransitList());
                break;
            case 3:
                fragment = PaidOrderFragment.newInstance(mOrderStatus.getPaidList());
                break;
            case 4:
                fragment = CancelOrderFragment.newInstance(mOrderStatus.getCancelList());
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUMBER_TAB;
    }
}
