package com.datn.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private final List<Banner> banners;
    private final Context context;
    private final IAction iActionBanner;


    public BannerAdapter(Context context, List<Banner> banners, IAction iActionBanner) {
        this.context = context;
        this.banners = banners;
        this.iActionBanner = iActionBanner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Banner banner = banners.get(position);
        Glide.with(context)
                .load(banner.getUrl())
                .into(holder.imgBanner);

        holder.itemView.setOnClickListener(v -> iActionBanner.onClick(banner));
    }

    @Override
    public int getItemCount() {
        if (banners != null) {
            return banners.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
            itemView.setOnClickListener(view -> {
                // làm màu tý :)
//                String action = list.get(getAdapterPosition()).getAction();
//                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
//                context.startActivity(browse);
            });
        }
    }
}
