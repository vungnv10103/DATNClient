package com.datn.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Product;
import com.datn.client.utils.Currency;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> products;
    private final Context context;
    private final IAction iActionProduct;

    public ProductAdapter(Context context, List<Product> dataList, IAction iActionProduct) {
        this.products = dataList;
        this.context = context;
        this.iActionProduct = iActionProduct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        if (product != null) {
            Glide.with(context).load(product.getImg_cover()).into(holder.imgProduct);
            holder.tvName.setText(product.getName());
            String price = product.getPrice();
            String formattedAmount = Currency.formatCurrency(price);
            holder.tvPrice.setText(formattedAmount);
            holder.tvStatus.setText(product.getStatus());
            holder.tvSold.setText("Đã bán: " + product.getSold());

            holder.itemView.setOnClickListener(v -> iActionProduct.onClick(product));
        }
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvStatus, tvSold, tvPrice;
        private final ImageView imgProduct;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSold = (TextView) itemView.findViewById(R.id.tv_sold);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
        }
    }
}
