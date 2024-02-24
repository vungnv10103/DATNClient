package com.datn.client.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.models.Product;
import com.datn.client.ui.product.DetailProductActivity;
import com.datn.client.utils.Currency;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;

public class ModalBottomSheet extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_add_to_cart, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Product currentProduct = DetailProductActivity.getProduct();
        final ShapeableImageView imgProduct = view.findViewById(R.id.img_product);
        final TextView tvName = view.findViewById(R.id.tv_name);
        final TextView tvPrice = view.findViewById(R.id.tv_price);
        final TextView tvQuantityStock = view.findViewById(R.id.tv_quantity_stock);
        Glide.with(this).load(currentProduct.getImg_cover()).into(imgProduct);
        tvName.setText(currentProduct.getName());
        tvPrice.setText(Currency.formatCurrency(currentProduct.getPrice()));
        tvQuantityStock.setText("Kho: " + currentProduct.getQuantity());
    }

    public static final String TAG = "AddToCartBottomSheet";
}
