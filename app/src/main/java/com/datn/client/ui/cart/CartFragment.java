package com.datn.client.ui.cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.adapter.CartAdapter;
import com.datn.client.databinding.FragmentCartBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.ProductCart;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.MyDialog;
import com.datn.client.ui.checkout.CheckoutActivity;
import com.datn.client.ui.product.ProductPresenter;
import com.datn.client.utils.Constants;
import com.datn.client.utils.Currency;
import com.datn.client.utils.PreferenceManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import java.util.List;

public class CartFragment extends Fragment implements ICartView {
    private FragmentCartBinding binding;
    private CartPresenter cartPresenter;
    private PreferenceManager preferenceManager;
    private ProgressBar progressBarCart;
    private NestedScrollView layoutCart;
    private TextView tvTotal;
    private CheckBox cbAllCart;
    private Button btnCheckout;

    private RecyclerView rcvCart;

    private Customer mCustomer;
    private String mToken;

    private CartAdapter cartAdapter;
    private List<ProductCart> mProductCart;
    private boolean isLoading = false;
    private int countCartSelected = 0;
    private int priceCartSelected = 0;


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

    }

    @Override
    public void onStart() {
        super.onStart();
        resetValue();
        cartPresenter.getDataCart();
        initEventClick();
    }


    @SuppressLint("SetTextI18n")
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
                cartAdapter = new CartAdapter(requireActivity(), mProductCart, cart -> {
                    String cartID = cart.get_id();
                    for (ProductCart productCart : mProductCart) {
                        if (productCart.get_id().equals(cartID)) {
                            System.out.println(productCart);
                            showToast(productCart.toString());
                        }
                    }
                }, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                linearLayoutManager.setSmoothScrollbarEnabled(true);
                rcvCart.setLayoutManager(new LinearLayoutManager(requireActivity()));
                rcvCart.setAdapter(cartAdapter);
                // ItemTouchHelper.SimpleCallback simpleCallback = new RecycleViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
                // new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvCart);
            } else {
                cartAdapter.updateList(mProductCart);
            }
            for (ProductCart productCart : mProductCart) {
                if (productCart.getStatus_cart() == ProductPresenter.STATUS_CART.SELECTED.getValue()) {
                    countCartSelected++;
                    priceCartSelected += Integer.parseInt(productCart.getPrice()) * Integer.parseInt(productCart.getQuantity_cart());
                }
            }
            if (mProductCart.size() == 0) {
                setLoading(false);
                return;
            }
            cbAllCart.setText("Tất cả(" + mProductCart.size() + ")");
            cbAllCart.setChecked(countCartSelected == mProductCart.size());
            tvTotal.setText(Currency.formatCurrency(String.valueOf(priceCartSelected)));
            setLoading(false);
        });
    }

    @Override
    public void onListCart(List<ProductCart> productCartList) {
        this.mProductCart = productCartList;
        displayCart();
    }

    @Override
    public void onThrowMessage(@NonNull String message) {
        setLoading(false);
        MyDialog.gI().startDlgOK(requireActivity(), message);
    }

    @Override
    public void onUpdateQuantity(String cartID, int position, String type, int value) {
        resetValue();
        setLoading(true);
        cartPresenter.updateQuantity(cartID, type, value);
    }

    @Override
    public void onUpdateStatus(String cartID, int position, int value) {
        resetValue();
        setLoading(true);
        cartPresenter.updateStatus(cartID, value);
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
        this.isLoading = isLoading;
        progressBarCart.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        layoutCart.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void resetValue() {
        priceCartSelected = 0;
        countCartSelected = 0;
    }

    private void initEventClick() {
        btnCheckout.setOnClickListener(v -> {
            if (!isLoading) {
                if (countCartSelected == 0) {
                    showToast("Vui lòng chọn sản phẩm để thanh toán!");
                } else {
                    startActivity(new Intent(requireActivity(), CheckoutActivity.class));
                }
            }
        });
        cbAllCart.setOnClickListener(v -> {
            resetValue();
            boolean isChecked = cbAllCart.isChecked();
            setLoading(true);
            cartPresenter.updateStatusAll(isChecked);
        });
    }

    private void initUI() {
        progressBarCart = binding.progressbarCart;
        layoutCart = binding.layoutCart;
        rcvCart = binding.rcvCart;
        tvTotal = binding.tvTotal;
        cbAllCart = binding.cbSelectedAll;
        btnCheckout = binding.btnCheckout;
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