package com.datn.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Product;
import com.datn.client.utils.MyFormat;
import com.datn.client.utils.STATUS_PRODUCT;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Product> products;
    private final int layoutItem;
    private static IAction iActionProduct;

    public ProductAdapter(Context context, List<Product> dataList, int layoutItem, IAction iActionProduct) {
        this.context = context;
        this.products = dataList;
        this.layoutItem = layoutItem;
        ProductAdapter.iActionProduct = iActionProduct;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutItem, parent, false);
        if (layoutItem == R.layout.item_product) {
            return new ViewHolderDefault(view);
        } else {
            return new ViewHolderSearch(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = products.get(position);
        if (layoutItem == R.layout.item_product) {
            ((ViewHolderDefault) holder).setData(context, product);
        } else {
            ((ViewHolderSearch) holder).setData(context, product);
        }
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }


    public static class ViewHolderDefault extends RecyclerView.ViewHolder {
        private final TextView tvName, tvStatus, tvSold, tvPrice;
        private final ShapeableImageView imgProduct;

        public ViewHolderDefault(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSold = itemView.findViewById(R.id.tv_sold);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        void setData(Context context, Product product) {
            if (product != null) {
                Glide.with(context)
                        .load(product.getImg_cover())
                        .error(R.drawable.logo_app_gradient)
                        .into(imgProduct);
                tvName.setText(product.getName());
                String price = product.getPrice();
                String formattedAmount = MyFormat.formatCurrency(price);
                tvPrice.setText(formattedAmount);
                String status = "";
                if (Integer.parseInt(product.getStatus()) == STATUS_PRODUCT.STOCKING.getValue()) {
                    status = context.getString(R.string.on_sale);
                } else if (Integer.parseInt(product.getStatus()) == STATUS_PRODUCT.OUT_OF_STOCK.getValue()) {
                    status = context.getString(R.string.temporarily_out_of_stock);
                }
                tvStatus.setText(status);
                tvSold.setText(String.format(context.getString(R.string.sold) + product.getSold()));

                itemView.setOnClickListener(v -> iActionProduct.onClick(product));
            }
        }
    }

    public static class ViewHolderSearch extends RecyclerView.ViewHolder {
        private final TextView tvName, tvPrice, tvSold;
        private final ShapeableImageView imgProduct;

        public ViewHolderSearch(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvSold = itemView.findViewById(R.id.tv_sold);
        }

        void setData(Context context, Product product) {
            if (product != null) {
                Glide.with(context)
                        .load(product.getImg_cover())
                        .error(R.drawable.logo_app_gradient)
                        .into(imgProduct);
                tvName.setText(product.getName());
                String price = product.getPrice();
                String formattedAmount = MyFormat.formatCurrency(price);
                tvPrice.setText(formattedAmount);
                tvSold.setText(String.format(context.getString(R.string.sold) + product.getSold()));

                itemView.setOnClickListener(v -> iActionProduct.onClick(product));
            }
        }
    }
}
