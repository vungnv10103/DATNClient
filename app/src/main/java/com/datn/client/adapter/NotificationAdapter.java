package com.datn.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Notification;
import com.datn.client.ui.BasePresenter.STATUS_NOTIFICATION;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notifications;
    private final Context context;
    private final IAction iActionNotification;
    private final Date currentTime = Calendar.getInstance().getTime();


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
                .into(holder.imgNotification);

        String status = context.getString(R.string.default_status_notification);
        if (notification.getStatus() == STATUS_NOTIFICATION.SEEN.getValue()) {
            status = context.getString(R.string.seen_notification);
        }
        holder.tvTitle.setText(String.format(notification.getTitle() + " " + status));
        holder.tvContent.setText(notification.getContent());
        holder.tvTime.setText(compareTime(notification.getCreated_at(), currentTime));

//        holder.itemView.setOnClickListener(v -> iActionNotification.onClick(notification));
        holder.itemView.setOnLongClickListener(v -> {
            iActionNotification.onClick(notification);
            return true;
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
        private final ShapeableImageView imgNotification;
        private final TextView tvTitle, tvContent, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNotification = itemView.findViewById(R.id.img_notification);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    public String compareTime(String timeReceive, Date currentTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        try {
            Date dateReceive = format.parse(timeReceive);
            if (dateReceive != null && currentTime != null) {
                long timeBetween = Math.abs(dateReceive.getTime() - currentTime.getTime());
                long hour = timeBetween / (60 * 60 * 1000);
                long minutes = timeBetween / (60 * 1000);
                long second = timeBetween / 1000;
                if (hour >= 1) {
                    return hour + context.getString(R.string.des_hour_time);
                } else if (minutes >= 1) {
                    return minutes + context.getString(R.string.des_minute_time);
                } else {
                    return second + context.getString(R.string.des_second_time);
                }
            }
            return timeReceive;
        } catch (ParseException e) {
            Log.d("NotificationAdapter", "compareTime: " + e.getMessage());
            return timeReceive;
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
