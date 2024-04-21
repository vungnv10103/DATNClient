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
import com.datn.client.databinding.FragmentOrderPrepareBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.OrdersDetail;
import com.datn.client.models.ProductOrder;
import com.datn.client.models.ProductOrderDetail;
import com.datn.client.models._BaseModel;
import com.datn.client.response.OrderResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;
import com.datn.client.utils.STATUS_ORDER;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrepareOrderFragment extends Fragment {
    private static final String TAG = PrepareOrderFragment.class.getSimpleName();
    private FragmentOrderPrepareBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private CircularProgressIndicator progressLoading;
    private RecyclerView rcvOrderPrepare;
    private Customer mCustomer;
    private String mToken;
    private Call<OrderResponse> getOrdersPrepare;
    private static List<ProductOrder> mPrepareOrders;

    @NonNull
    public static PrepareOrderFragment newInstance() {
        return new PrepareOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderPrepareBinding.inflate(getLayoutInflater());
        initUI();
        checkRequire();
        apiService = RetrofitConnection.getApiService();
        return binding.getRoot();
    }

    private void checkRequire() {
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().getCustomerLogin(requireActivity());
        mToken = ManagerUser.gI().checkToken(requireActivity());
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getOrdersPrepare();

    }

    private void getOrdersPrepare() {
        requireActivity().runOnUiThread(() -> {
            try {
                getOrdersPrepare = apiService.getOrdersByStatus(mToken, mCustomer.get_id(), STATUS_ORDER.PREPARE.getValue());
                getOrdersPrepare.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                showLogW("getAllOrders200", code);
                                OrdersDetail ordersDetail = response.body().getOrdersDetail();
                                if (ordersDetail != null) {
                                    displayOrder(OrderActivity.getProductOrderDetail(ordersDetail.getPrepareList()));
                                }
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    reLogin();
                                } else {
                                    showLogW("getAllOrders400", code);
                                    MyDialog.gI().startDlgOK(requireActivity(), message.getTitle(), message.getContent());
                                }
                            }
                        } else {
                            showLogW("getAllOrders: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                        showLogW("getAllOrders: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                showLogW("getAllOrders", e.getMessage());
            }
        });
    }

    private void displayOrder(List<ProductOrderDetail> productOrderDetails) {
        if (productOrderDetails != null) {
            OrderActivity.displayCustomBadge(OrderActivity.POSITION_PREPARE_TAB, productOrderDetails.size(), -1);
            OrderAdapter orderAdapter = new OrderAdapter(requireActivity(), productOrderDetails, new IAction() {
                @Override
                public void onClick(_BaseModel orderPrepare) {
                    MyDialog.gI().startDlgOK(requireActivity(), orderPrepare.get_id());
                }

                @Override
                public void onLongClick(_BaseModel orderPrepare) {
                    MyDialog.gI().startDlgOK(requireActivity(), "status: " + orderPrepare.get_id());
                }

                @Override
                public void onItemClick(_BaseModel orderPrepare) {
                    MyDialog.gI().startDlgOK(requireActivity(), "status: " + orderPrepare.get_id());
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            rcvOrderPrepare.setLayoutManager(linearLayoutManager);
            rcvOrderPrepare.setAdapter(orderAdapter);
        }
        rcvOrderPrepare.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);
    }

    private void initUI() {
        progressLoading = binding.progressbarOrderPrepare;
        rcvOrderPrepare = binding.rcvOrderPrepare;
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }


    private void showToast(@NonNull Object message) {
        Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }

    private void onCancelAPI() {
        if (getOrdersPrepare != null) {
            getOrdersPrepare.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        onCancelAPI();
    }
}