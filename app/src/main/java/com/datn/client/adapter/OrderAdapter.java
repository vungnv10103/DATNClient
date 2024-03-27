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
import com.datn.client.models.Order;
import com.datn.client.models.Product;
import com.datn.client.models.ProductOrder;
import com.datn.client.models._BaseModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<ProductOrder> productOrders;
    private final Context context;
    private final IAction iActionOrder;


    public OrderAdapter(Context context, List<ProductOrder> productOrders, IAction iActionOrder) {
        this.context = context;
        this.productOrders = productOrders;
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
        ProductOrder productOrder = productOrders.get(position);

        List<Product> product = productOrder.getProducts();
        List<String> productsQuantity = productOrder.getProductsQuantity();
        List<String> orderDetailID = productOrder.getOrderDetailID();

        Glide.with(context)
                .load(product.get(position).getImg_cover())
                .error(R.drawable.logo_app_gradient)
                .into(holder.imgProductOrder);

        holder.tvName.setText(product.get(position).getName());
        holder.tvPrice.setText(productOrder.getAmount());
        holder.tvQuantity.setText(productsQuantity.get(position));


        _BaseModel baseModel = new _BaseModel(orderDetailID.get(position), productOrder.getCreated_at());
        holder.itemView.setOnClickListener(v -> iActionOrder.onClick(baseModel));
    }

    @Override
    public int getItemCount() {
        if (productOrders != null) {
            return productOrders.size();
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
