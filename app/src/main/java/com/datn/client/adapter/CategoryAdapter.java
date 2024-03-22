package com.datn.client.adapter;

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
import com.datn.client.models.Category;
import com.datn.client.ui.home.HomeFragment;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.categoriesViewHolder> {

    private final Context context;
    private final List<Category> listCate;
    private final IAction iActionCate;

    public CategoryAdapter(Context context, List<Category> categories, IAction iActionCate) {
        this.context = context;
        listCate = categories;
        this.iActionCate = iActionCate;
    }

    @NonNull
    @Override
    public categoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new categoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoriesViewHolder holder, int position) {
        Category category = listCate.get(position);
        holder.tvName.setText(category.getName());
        Glide.with(context)
                .load(category.getImage())
                .error(R.drawable.logo_app_gradient)
                .into(holder.img);
        if (category.get_id().equals("-1")) {
            int paddingImage = 40;
            holder.img.setPadding(paddingImage, paddingImage, paddingImage, paddingImage);
        }
        if (position >= 12) {
            if (HomeFragment.isDisableItemCate) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        holder.itemView.setOnClickListener(v -> iActionCate.onClick(category));
    }

    @Override
    public int getItemCount() {
        if (listCate != null) {
            return listCate.size();
        }
        return 0;
    }

    public static class categoriesViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView img;

        public categoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_cate);
            tvName = itemView.findViewById(R.id.tv_title);
        }
    }
}
