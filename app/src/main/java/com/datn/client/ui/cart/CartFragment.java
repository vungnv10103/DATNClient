package com.datn.client.ui.cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.datn.client.R;
import com.datn.client.databinding.FragmentCartBinding;
import com.datn.client.databinding.FragmentHomeBinding;
import com.datn.client.models.Cart;
import com.datn.client.models.Customer;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.MyDialog;
import com.datn.client.ui.home.HomePresenter;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.List;

public class CartFragment extends Fragment implements ICartView {
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

    @Override
    public void onListCart(List<Cart> cartList) {
        showToast(cartList.toString());
    }

    @Override
    public void onThrowMessage(String message) {
        MyDialog.gI().startDlgOK(requireActivity(), message);
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
        cartPresenter = new CartPresenter(this, apiService, mToken, mCustomer.get_id());
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