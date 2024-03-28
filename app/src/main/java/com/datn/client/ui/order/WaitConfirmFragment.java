package com.datn.client.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.adapter.OrderAdapter;
import com.datn.client.databinding.FragmentOrderWaitingBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.ProductOrder;
import com.datn.client.models.ProductOrderDetail;
import com.datn.client.models._BaseModel;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

public class WaitConfirmFragment extends Fragment {
    private static final String TAG = WaitConfirmFragment.class.getSimpleName();

    private FragmentOrderWaitingBinding binding;
    private OrderPresenter orderPresenter;

    private PreferenceManager preferenceManager;

    private CircularProgressIndicator progressLoading;
    private RecyclerView rcvOrderWaiting;

    private Customer mCustomer;
    private String mToken;
    private static List<ProductOrder> mWaitingOrders;

    @NonNull
    public static WaitConfirmFragment newInstance(List<ProductOrder> dataOrder) {
        mWaitingOrders = dataOrder;
        return new WaitConfirmFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderWaitingBinding.inflate(inflater, container, false);
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
    public void onStart() {
        super.onStart();

        displayOrder(OrderActivity.getProductOrderDetail(mWaitingOrders));
    }

    private void displayOrder(List<ProductOrderDetail> productOrdersDetail) {
        if (productOrdersDetail != null) {
            OrderActivity.displayCustomBadge(OrderActivity.POSITION_WAITING_TAB, productOrdersDetail.size(), -1);
            OrderAdapter orderAdapter = new OrderAdapter(requireActivity(), productOrdersDetail, new IAction() {
                @Override
                public void onClick(_BaseModel orderWaiting) {
                    MyDialog.gI().startDlgOK(requireActivity(), orderWaiting.get_id());
                }

                @Override
                public void onLongClick(_BaseModel orderWaiting) {
                    MyDialog.gI().startDlgOK(requireActivity(), "status: " +orderWaiting.get_id());
                }

                @Override
                public void onItemClick(_BaseModel orderWaiting) {

                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            rcvOrderWaiting.setLayoutManager(linearLayoutManager);
            rcvOrderWaiting.setAdapter(orderAdapter);
        }

        rcvOrderWaiting.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);
    }

    private void initUI() {
        progressLoading = binding.progressbarOrderWaiting;
        rcvOrderWaiting = binding.rcvOrderWaiting;
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }
}