package com.datn.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.models.ProductCart;
import com.datn.client.utils.MyFormat;

import java.util.List;

public class ProductCheckoutAdapter extends RecyclerView.Adapter<ProductCheckoutAdapter.ViewHolder> {
    private final List<ProductCart> productCarts;
    private final Context context;

    public ProductCheckoutAdapter(Context context, List<ProductCart> dataProductCart) {
        this.productCarts = dataProductCart;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_checkout, parent, false);
        return new ProductCheckoutAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCart productCart = productCarts.get(position);
        holder.tvName.setText(productCart.getName());
        Glide.with(context)
                .load(productCart.getImage())
                .error(R.drawable.logo_app_gradient)
                .into(holder.imgProduct);
        int quantityCart = Integer.parseInt(productCart.getQuantity_cart());
        int quantityProduct = Integer.parseInt(productCart.getQuantity_product());
        Log.d("ProductCheckoutAdapter", "quantityProduct: " + quantityProduct);
        int priceOne = Integer.parseInt(productCart.getPrice());
        holder.tvPrice.setText(MyFormat.formatCurrency(String.valueOf(priceOne * quantityCart)));
        holder.tvQuantity.setText("Số lượng: " + quantityCart);
        holder.tvOptions.setText(productCart.getCreated_at());
    }


    @Override
    public int getItemCount() {
        return productCarts == null ? 0 : productCarts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProduct;
        private final TextView tvName, tvQuantity, tvPrice, tvOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvOptions = itemView.findViewById(R.id.tv_options);
        }
    }

}
