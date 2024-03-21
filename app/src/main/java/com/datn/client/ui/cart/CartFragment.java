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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.R;
import com.datn.client.adapter.CartAdapter;
import com.datn.client.databinding.FragmentCartBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.ProductCart;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyNavController;
import com.datn.client.ui.checkout.CheckoutActivity;
import com.datn.client.ui.product.ProductPresenter.STATUS_CART;
import com.datn.client.utils.Constants;
import com.datn.client.utils.Currency;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import java.util.List;

public class CartFragment extends Fragment implements ICartView {
    private FragmentCartBinding binding;
    private CartPresenter cartPresenter;
    private PreferenceManager preferenceManager;
    private CircularProgressIndicator progressBarCart;

    private TextView tvTotal;
    private CheckBox cbAllCart;
    private Button btnCheckout, btnGoShopping;

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
        requireActivity().runOnUiThread(() -> {
            preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
            initUI();
            checkLogin();
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().runOnUiThread(() -> {
            setLoading(true);
            initService();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().runOnUiThread(() -> {
            setLoading(true);
            resetValue();
            cartPresenter.getDataCart();
            initEventClick();
        });
    }


    @SuppressLint("SetTextI18n")
    private void displayCart() {
        requireActivity().runOnUiThread(() -> {
            resetValue();
            if (getContext() != null && getContext() instanceof Activity && !((Activity) getContext()).isFinishing()) {
                if (mProductCart.isEmpty()) {
                    if (cartAdapter != null) {
                        System.out.println("displayCart");
                        cartAdapter.updateList(mProductCart);
                    }
                }
            }
            if (cartAdapter == null) {
                cartAdapter = new CartAdapter(requireActivity(), mProductCart, cart -> {

                }, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                linearLayoutManager.setSmoothScrollbarEnabled(true);
                rcvCart.setLayoutManager(linearLayoutManager);
                rcvCart.setAdapter(cartAdapter);
                // ItemTouchHelper.SimpleCallback simpleCallback = new RecycleViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
                // new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvCart);
            } else {
                cartAdapter.updateList(mProductCart);
            }
            for (ProductCart productCart : mProductCart) {
                if (productCart.getStatus_cart() == STATUS_CART.SELECTED.getValue()) {
                    countCartSelected++;
                    priceCartSelected += Integer.parseInt(productCart.getPrice()) * Integer.parseInt(productCart.getQuantity_cart());
                }
            }
            if (mProductCart.isEmpty()) {
                setLoading(false);
                setLayoutEmpty(true);
                return;
            }
            setLayoutEmpty(false);
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
    public void onThrowMessage(@NonNull MessageResponse message) {
        setLoading(false);
        MyDialog.gI().startDlgOK(requireActivity(), message.getTitle(), message.getContent());
    }

    @Override
    public void onThrowMessage(@NonNull String message) {
        setLoading(false);
        MyDialog.gI().startDlgOK(requireActivity(), message);
    }

    @Override
    public void onUpdateQuantity(String cartID, int position, String type, int value) {
        requireActivity().runOnUiThread(() -> {
            setLoading(true);
            cartPresenter.updateQuantity(cartID, type, value);
        });
    }

    @Override
    public void onUpdateStatus(String cartID, int position, int value) {
        requireActivity().runOnUiThread(() -> {
            setLoading(true);
            cartPresenter.updateStatus(cartID, value);
        });
    }

    @Override
    public void onBuyNow(String cartID) {
        cartPresenter.buyNowCart(requireActivity(), cartID);
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            switchToLogin();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            switchToLogin();
        }
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        cartPresenter = new CartPresenter(this, apiService, mToken, mCustomer.get_id());
    }

    private void setLayoutEmpty(boolean isEmpty) {
        if (isEmpty) {
            binding.layoutCartEmpty.setVisibility(View.VISIBLE);
            binding.layoutPrice.setVisibility(View.GONE);
            binding.layoutCart.setVisibility(View.GONE);
        } else {
            binding.layoutCartEmpty.setVisibility(View.GONE);
            binding.layoutPrice.setVisibility(View.VISIBLE);
            binding.layoutCart.setVisibility(View.VISIBLE);
        }
    }

    private void setLoading(boolean isLoading) {
        requireActivity().runOnUiThread(() -> {
            this.isLoading = isLoading;
            progressBarCart.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.layoutPrice.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
            binding.layoutCart.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void resetValue() {
        priceCartSelected = 0;
        countCartSelected = 0;
    }

    private void initEventClick() {
        btnCheckout.setOnClickListener(v -> requireActivity().runOnUiThread(() -> {
            if (!isLoading) {
                if (countCartSelected == 0) {
                    showToast("Vui lòng chọn sản phẩm để thanh toán!");
                } else {
                    startActivity(new Intent(requireActivity(), CheckoutActivity.class));
                }
            }
        }));
        cbAllCart.setOnClickListener(v -> requireActivity().runOnUiThread(() -> {
            boolean isChecked = cbAllCart.isChecked();
            setLoading(true);
            requireActivity().runOnUiThread(() -> cartPresenter.updateStatusAll(isChecked));
        }));
        btnGoShopping.setOnClickListener(v -> MyNavController.gI().navigateFragment(requireActivity(), R.id.navigation_home));
    }

    private void initUI() {
        progressBarCart = binding.progressbarCart;
        rcvCart = binding.rcvCart;
        tvTotal = binding.tvTotal;
        cbAllCart = binding.cbSelectedAll;
        btnCheckout = binding.btnCheckout;
        btnGoShopping = binding.btnGoShopping;
    }

    private void switchToLogin() {
        showToast(getString(R.string.please_log_in_again));
        requireActivity().finishAffinity();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        cartPresenter.cancelAPI();
    }
}