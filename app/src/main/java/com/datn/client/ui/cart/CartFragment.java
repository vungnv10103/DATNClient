package com.datn.client.ui.cart;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.adapter.CartAdapter;
import com.datn.client.databinding.FragmentCartBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.ProductCart;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import java.util.List;

public class CartFragment extends Fragment implements ICartView {
    private FragmentCartBinding binding;
    private CartPresenter cartPresenter;
    private PreferenceManager preferenceManager;
    private SpinKitView spinKitView;
    private NestedScrollView layoutCart;
    private AppBarLayout appbarSearch;

    private RecyclerView rcvCart, rcvCartSearch;

    private Customer mCustomer;
    private String mToken;

    private CartAdapter cartAdapter;
    private List<ProductCart> mProductCart;
    public int posCartSelected = -1;

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

        setLoading(true);
        initService();
        cartPresenter.getDataCart();
    }

    private void displayCart() {
        requireActivity().runOnUiThread(() -> {
            if (getContext() != null && getContext() instanceof Activity && !((Activity) getContext()).isFinishing()) {
                if (mProductCart.size() == 0) {
                    if (cartAdapter != null) {
                        System.out.println("displayCart");
                        cartAdapter.updateList(mProductCart);
                    }
                }
            }
            if (cartAdapter == null) {
                cartAdapter = new CartAdapter(requireActivity(), mProductCart, cart -> showToast(cart.get_id()), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                // linearLayoutManager.setSmoothScrollbarEnabled(true);
                rcvCartSearch.setLayoutManager(linearLayoutManager);
                rcvCartSearch.setAdapter(cartAdapter);
                rcvCart.setLayoutManager(new LinearLayoutManager(requireActivity()));
                rcvCart.setAdapter(cartAdapter);
                // ItemTouchHelper.SimpleCallback simpleCallback = new RecycleViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
                // new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvCart);
            } else {
                cartAdapter.updateList(mProductCart);
            }
            setLoading(false);
        });
    }

    @Override
    public void onListCart(List<ProductCart> productCartList) {
        this.mProductCart = productCartList;
        displayCart();
    }

    @Override
    public void onThrowMessage(String message) {
        setLoading(false);
        MyDialog.gI().startDlgOK(requireActivity(), message);
    }

    @Override
    public void onUpdateQuantity(String cartID, int position, String type, int value) {
        setLoading(true);
        posCartSelected = position;
        cartPresenter.updateQuantity(cartID, type, value);
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

    private void setLoading(boolean isLoading) {
        spinKitView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        layoutCart.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        appbarSearch.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void initUI() {
        spinKitView = binding.spinKitLoading;
        layoutCart = binding.layoutCart;
        appbarSearch = binding.appbarSearch;
        rcvCartSearch = binding.list;
        rcvCart = binding.rcvCart;
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