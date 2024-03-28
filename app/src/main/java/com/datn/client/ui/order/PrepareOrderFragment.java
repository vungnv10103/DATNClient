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
import com.datn.client.models.Product;
import com.datn.client.models.ProductOrder;
import com.datn.client.models.ProductOrderDetail;
import com.datn.client.models._BaseModel;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PrepareOrderFragment extends Fragment {
    private static final String TAG = PrepareOrderFragment.class.getSimpleName();

    private FragmentOrderPrepareBinding binding;

    private PreferenceManager preferenceManager;

    private CircularProgressIndicator progressLoading;
    private RecyclerView rcvOrderPrepare;

    private Customer mCustomer;
    private String mToken;
    private static List<ProductOrder> mPrepareOrders;

    @NonNull
    public static PrepareOrderFragment newInstance(List<ProductOrder> dataOrder) {
        mPrepareOrders = dataOrder;
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

        displayOrder(getProductOrderDetail());
    }

    private List<ProductOrderDetail> getProductOrderDetail() {
        List<ProductOrderDetail> dataProductOrderDetail = new ArrayList<>();
        ProductOrderDetail productOrderDetail = new ProductOrderDetail();
        for (int i = 0; i < mPrepareOrders.size(); i++) {
            ProductOrder productOrder = mPrepareOrders.get(i);
            List<Product> products = productOrder.getProducts();
            List<String> productsQuantity = productOrder.getProductsQuantity();
            List<String> orderDetailID = productOrder.getOrderDetailID();

            for (int j = 0; j < products.size(); j++) {
                Product product = products.get(j);
                productOrderDetail.setQuantity(Integer.parseInt(productsQuantity.get(j)));
                productOrderDetail.setOrder_id(orderDetailID.get(j));
                productOrderDetail.setProduct_id(product.get_id());
                productOrderDetail.setProduct_name(product.getName());
                productOrderDetail.setProduct_image(product.getImg_cover());
                dataProductOrderDetail.add(productOrderDetail);
            }
        }
        return dataProductOrderDetail;
    }

    private void displayOrder(List<ProductOrderDetail> productOrderDetails) {
        OrderAdapter orderAdapter = new OrderAdapter(requireActivity(), productOrderDetails, new IAction() {
            @Override
            public void onClick(_BaseModel orderWaiting) {
                MyDialog.gI().startDlgOK(requireActivity(), orderWaiting.get_id());
            }

            @Override
            public void onLongClick(_BaseModel orderWaiting) {

            }

            @Override
            public void onItemClick(_BaseModel orderWaiting) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvOrderPrepare.setLayoutManager(linearLayoutManager);
        rcvOrderPrepare.setAdapter(orderAdapter);
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


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }
}