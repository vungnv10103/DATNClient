package com.datn.client.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.client.R;
import com.datn.client.models.Order;
import com.datn.client.models.ProductOrder;

import java.util.List;

public class CancelOrderFragment extends Fragment {
    private static List<ProductOrder> mCancelOrders;

    @NonNull
    public static CancelOrderFragment newInstance(List<ProductOrder> dataOrder) {
        mCancelOrders = dataOrder;
        return new CancelOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_cancel, container, false);
    }
}