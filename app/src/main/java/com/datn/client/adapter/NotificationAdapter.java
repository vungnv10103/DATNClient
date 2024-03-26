package com.datn.client.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Notification;
import com.datn.client.ui.BasePresenter.STATUS_NOTIFICATION;
import com.datn.client.utils.MyFormat;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notifications;
    private final Context context;
    private final IAction iActionNotification;
    private final Date currentTime = Calendar.getInstance().getTime();
    public static boolean isShowSelected = false;
    public static boolean isChecked = false;


    public NotificationAdapter(Context context, List<Notification> notifications, IAction iActionNotification) {
        this.context = context;
        this.notifications = notifications;
        this.iActionNotification = iActionNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        Glide.with(context)
                .load(notification.getImage())
                .error(R.drawable.logo_app_gradient)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        Log.w("NotificationAdapter", e != null ? e.getMessage() : "Error load image notification");
                        holder.progressLoadingImage.setVisibility(View.GONE);
                        holder.imgNotification.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        holder.progressLoadingImage.setVisibility(View.GONE);
                        holder.imgNotification.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.imgNotification);

        String status = context.getString(R.string.default_status_notification);
        if (notification.getStatus() == STATUS_NOTIFICATION.SEEN.getValue()) {
            status = "";
        }
        holder.tvTitle.setText(notification.getTitle());
        holder.tvStatus.setText(status);
        holder.tvContent.setText(notification.getContent());
        holder.tvTime.setText(MyFormat.compareTime(context, notification.getCreated_at(), currentTime));
        holder.cbSelected.setVisibility(isShowSelected ? View.VISIBLE : View.GONE);
        holder.cbSelected.setChecked(notification.isChecked());

        holder.itemView.setOnClickListener(v -> iActionNotification.onClick(notification));
        holder.itemView.setOnLongClickListener(v -> {
            isShowSelected = !isShowSelected;
            iActionNotification.onLongClick(notification);
            holder.cbSelected.setChecked(true);
            notifyItemRangeChanged(0, getItemCount());
            return true;
        });
        holder.cbSelected.setOnClickListener(v -> {
            isChecked = holder.cbSelected.isChecked();
            iActionNotification.onItemClick(notification);
        });

    }

    @Override
    public int getItemCount() {
        if (notifications != null) {
            return notifications.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircularProgressIndicator progressLoadingImage;
        private final ShapeableImageView imgNotification;
        private final TextView tvTitle, tvContent, tvStatus, tvTime;
        private final CheckBox cbSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressLoadingImage = itemView.findViewById(R.id.progressbar_loading_image);
            imgNotification = itemView.findViewById(R.id.img_notification);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTime = itemView.findViewById(R.id.tv_time);
            cbSelected = itemView.findViewById(R.id.cb_selected);
        }
    }


    public void updateList(List<Notification> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NotificationAdapter.CartDiffCallback(newList, notifications));
        int oldSize = notifications.size();
        notifications.clear();
        notifications.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        int newSize = newList.size();
        System.out.println("oldSize: " + oldSize + " - newSize:" + newSize);
    }

    private static class CartDiffCallback extends DiffUtil.Callback {
        private final List<Notification> oldNotificationList;
        private final List<Notification> newNotificationList;

        public CartDiffCallback(List<Notification> newNotificationList, List<Notification> oldNotificationList) {
            this.newNotificationList = newNotificationList;
            this.oldNotificationList = oldNotificationList;
        }

        @Override
        public int getOldListSize() {
            return oldNotificationList.size();
        }

        @Override
        public int getNewListSize() {
            return newNotificationList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldNotificationList.get(oldItemPosition).get_id(), newNotificationList.get(newItemPosition).get_id());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Notification oldNotification = oldNotificationList.get(oldItemPosition);
            Notification newNotification = newNotificationList.get(newItemPosition);
            return oldNotification.getTitle().equals(newNotification.getTitle())
                    && Objects.equals(oldNotification.getContent(), newNotification.getContent())
                    && Objects.equals(oldNotification.getCreated_at(), newNotification.getCreated_at())
                    && Objects.equals(oldNotification.getStatus(), newNotification.getStatus())
                    && Objects.equals(oldNotification.getImage(), newNotification.getImage());
        }
    }
}
