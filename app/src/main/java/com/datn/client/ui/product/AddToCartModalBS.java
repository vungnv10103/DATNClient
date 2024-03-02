package com.datn.client.ui.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.databinding.BottomsheetAddToCartBinding;
import com.datn.client.models.Product;
import com.datn.client.services.ApiService;
import com.datn.client.ui.MyDialog;
import com.datn.client.utils.Currency;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AddToCartModalBS extends BottomSheetDialogFragment implements IProductView {
    private BottomsheetAddToCartBinding binding;
    public static final String TAG = "AddToCartBottomSheet";

    private ProductPresenter productPresenter;
    private ShapeableImageView imgProduct;
    private TextView tvName, tvPrice, tvQuantityStock, tvQuantity, tvPriceTemp;
    private MaterialButton btnMinus, btnPlus, btnAddToCart;
    private Product mProduct;

    private final ApiService apiService;
    private final String token;
    private final String customerID;
    private final int mType;

    public AddToCartModalBS(ApiService apiService, String token, String customerID, int type) {
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
        this.mType = type;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomsheetAddToCartBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initService();
        mProduct = DetailProductActivity.getProduct();
        if (mProduct != null) {
            Glide.with(this).load(mProduct.getImg_cover()).into(imgProduct);
            tvName.setText(mProduct.getName());
            tvPrice.setText("Giá: " + Currency.formatCurrency(mProduct.getPrice()));
            int quantityProduct = Integer.parseInt(mProduct.getQuantity());
            tvQuantityStock.setText("Kho: " + quantityProduct);
            if (quantityProduct <= 0) {
                tvQuantity.setText("0");
//                btnPlus.setIconTintResource(R.color.gray_400);
                btnMinus.setEnabled(false);
                btnPlus.setEnabled(false);
                tvPriceTemp.setText("Tạm tính: " + Currency.formatCurrency("0"));
                btnAddToCart.setEnabled(false);
            } else {
                tvPriceTemp.setText("Tạm tính: " + Currency.formatCurrency(mProduct.getPrice()));
            }
            if (mType == 0) {
                btnAddToCart.setText(getString(R.string.buy_now));
            } else if (mType == 1) {
                btnAddToCart.setText(getString(R.string.add_to_cart));
            }
            initEventClick();
        }
    }

    private void initService() {
        productPresenter = new ProductPresenter(this, apiService, token, customerID);
    }

    private void initEventClick() {
        btnAddToCart.setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString().trim());
            String notes = ""; // custom note
            if (mType == DetailProductActivity.TYPE_BUY.ADD_TO_CART.getValue()) {
                productPresenter.addToCart(mProduct.get_id(), quantity, notes);
            } else if (mType == DetailProductActivity.TYPE_BUY.BUY_NOW.getValue()) {
                productPresenter.buyNow(requireActivity(), mProduct.get_id(), quantity, notes);
            }

        });
        btnMinus.setOnClickListener(v -> setBtnMinus());
        btnPlus.setOnClickListener(v -> setBtnPlus());
    }

    @SuppressLint("SetTextI18n")
    private void setBtnMinus() {
        try {
            int quantity = Integer.parseInt(tvQuantity.getText().toString().trim());
            quantity--;
            if (quantity <= 1) {
                quantity = 1;
                btnMinus.setIconTintResource(R.color.gray_400);
            }
            btnPlus.setIconTintResource(R.color.big_stone);
            tvQuantity.setText(String.valueOf(quantity));
            tvPriceTemp.setText("Tạm tính: " + Currency.formatCurrency(String.valueOf(Integer.parseInt(mProduct.getPrice()) * quantity)));
        } catch (NumberFormatException e) {
            Log.w(TAG, "minus: " + e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void setBtnPlus() {
        try {
            int quantity = Integer.parseInt(tvQuantity.getText().toString().trim());
            quantity++;
            //  set limit quantity: quantity or 20
            if (quantity >= Math.min(Integer.parseInt(mProduct.getQuantity()), 20)) {
                quantity = Math.min(Integer.parseInt(mProduct.getQuantity()), 20);
                btnPlus.setIconTintResource(R.color.gray_400);
            }
            btnMinus.setIconTintResource(R.color.big_stone);
            tvQuantity.setText(String.valueOf(quantity));
            tvPriceTemp.setText("Tạm tính: " + Currency.formatCurrency(String.valueOf(Integer.parseInt(mProduct.getPrice()) * quantity)));
        } catch (NumberFormatException e) {
            Log.w(TAG, "plus: " + e.getMessage());
        }
    }

    private void initView() {
        imgProduct = binding.imgProduct;
        tvName = binding.tvName;
        tvPrice = binding.tvPrice;
        tvQuantityStock = binding.tvQuantityStock;
        btnMinus = binding.btnMinus;
        tvQuantity = binding.tvQuantity;
        btnPlus = binding.btnPlus;
        btnAddToCart = binding.btnAddToCart;
        tvPriceTemp = binding.tvPriceTemp;
    }

    private void showToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadProduct(List<Product> productList) {

    }

    @Override
    public void onThrowMessage(@NonNull String code) {
        switch (code) {
            case "cart/add-success":
                showToast("Đã thêm vào giỏ hàng");
                this.dismiss();
                break;
            case "cart/update-quantity-success":
                showToast("Cập nhật số lượng thành công");
                this.dismiss();
                break;
            case "cart/update-quantity-failed":
                showToast("Sản phẩm đã có trong giỏ hàng\nSố lượng vượt quá giới hạn");
                this.dismiss();
                break;
            case "cart/product-is-temporarily-out-of-stock":
                showToast("Sản phẩm tạm hết hàng, không thể đặt");
                this.dismiss();
                break;
            default:
                MyDialog.gI().startDlgOK(requireActivity(), code);
                break;
        }
    }
}
