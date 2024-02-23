package com.datn.client.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.datn.client.databinding.FragmentCartBinding;
import com.datn.client.models.Customer;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.cart.CartPresenter;
import com.datn.client.ui.cart.ICartView;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

public class BaseFragment extends Fragment{
    private FragmentCartBinding binding;
    private CartPresenter cartPresenter;
    private PreferenceManager preferenceManager;

    private Customer mCustomer;
    private String mToken;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        initUI();
        checkLogin();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initService();
    }


    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            requireActivity().finishAffinity();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            requireActivity().finishAffinity();
        }
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
//        cartPresenter = new CartPresenter(this, apiService, mToken, mCustomer.get_id());
    }

    private void initUI() {
    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}