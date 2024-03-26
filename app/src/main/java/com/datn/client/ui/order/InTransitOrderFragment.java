package com.datn.client.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.datn.client.R;

public class InTransitOrderFragment extends Fragment {
    public InTransitOrderFragment() {
        // Required empty public constructor
    }
    @NonNull
    public static InTransitOrderFragment newInstance() {
        return new InTransitOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancel_order, container, false);
    }
}