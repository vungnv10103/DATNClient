package com.datn.client.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.ProductOrderDetail;
import com.datn.client.models._BaseModel;
import com.datn.client.utils.MyFormat;
import com.datn.client.utils.STATUS_ORDER;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<ProductOrderDetail> productOrdersDetail;
    private final Context context;
    private final IAction iActionOrder;


    public OrderAdapter(Context context, List<ProductOrderDetail> productOrdersDetail, IAction iActionOrder) {
        this.context = context;
        this.productOrdersDetail = productOrdersDetail;
        this.iActionOrder = iActionOrder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductOrderDetail productOrderDetail = productOrdersDetail.get(position);

        Glide.with(context)
                .load(productOrderDetail.getProduct_image())
                .error(R.drawable.logo_app_gradient)
                .into(holder.imgProductOrder);

        holder.tvName.setText(productOrderDetail.getProduct_name());
        holder.tvPrice.setText(MyFormat.formatCurrency(productOrderDetail.getPrice()));
        holder.tvQuantity.setText(String.valueOf(productOrderDetail.getQuantity()));

        _BaseModel baseModel = new _BaseModel(productOrderDetail.getOrder_detail_id(), productOrderDetail.getCreated_at());
        _BaseModel baseModel2 = new _BaseModel(String.valueOf(productOrderDetail.getStatus()), productOrderDetail.getCreated_at());
        holder.itemView.setOnClickListener(v -> iActionOrder.onClick(baseModel));
        Log.d("OrderAdapter", "onBindViewHolder: " + productOrderDetail.getStatus());
        if (productOrderDetail.getStatus() == STATUS_ORDER.PAID.getValue()) {

            holder.itemView.setOnLongClickListener(v -> {
                iActionOrder.onLongClick(baseModel2);
                return true;
            });
        }


    }

    @Override
    public int getItemCount() {
        if (productOrdersDetail != null) {
            return productOrdersDetail.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView imgProductOrder;
        private final TextView tvName, tvPrice, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductOrder = itemView.findViewById(R.id.img_product_order);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}
