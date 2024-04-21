package com.datn.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageModel;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.TYPE_MESSAGE;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<MessageModel> dataMessage;
    private final Customer customerLogged;
    private final Context context;
    private final IAction iActionMsg;
    private final Date currentTime = Calendar.getInstance().getTime();
    private final String urlAvatarUserFocus;

    public MessageAdapter(Context context, List<MessageModel> dataMessage, String urlAvatarUserFocus, IAction iActionMsg) {
        this.context = context;
        customerLogged = ManagerUser.gI().getCustomerLogin((FragmentActivity) context);
        this.urlAvatarUserFocus = urlAvatarUserFocus;
        this.dataMessage = dataMessage;
        this.iActionMsg = iActionMsg;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MessageModel.VIEW_TYPE_SENT) {
            View viewSent = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new SentMsgViewHolder(viewSent);
        } else {
            View viewReceived = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ReceivedMsgViewHolder(viewReceived);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == MessageModel.VIEW_TYPE_SENT) {
            ((SentMsgViewHolder) holder).setData(dataMessage.get(position), context, iActionMsg);
        } else {
            ((ReceivedMsgViewHolder) holder).setData(dataMessage.get(position), context, iActionMsg, urlAvatarUserFocus);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataMessage.get(position).getSender_id().equals(customerLogged.get_id())) {
            return MessageModel.VIEW_TYPE_SENT;
        } else {
            return MessageModel.VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        if (dataMessage != null) {
            return dataMessage.size();
        }
        return 0;
    }

    static class SentMsgViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessage, tvDateTime;
        private final ShapeableImageView imgMsg;

        public SentMsgViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDateTime = itemView.findViewById(R.id.tvTime);
            imgMsg = itemView.findViewById(R.id.img_msg);
        }

        void setData(MessageModel dataMsg, Context context, IAction iActionMessage) {
            if (dataMsg != null) {
                int msgType = dataMsg.getMessage_type();
                if (msgType == TYPE_MESSAGE.TEXT.getValue()) {
                    tvMessage.setText(dataMsg.getMessage());
                } else if (msgType == TYPE_MESSAGE.IMAGE.getValue()) {
                    Glide.with(context)
                            .load(dataMsg.getMessage())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(imgMsg);
                }

                String dataTime = dataMsg.getCreated_at();
                dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
                tvDateTime.setText(dataTime);

                itemView.setOnClickListener(v -> iActionMessage.onClick(dataMsg));
            }
        }
    }

    static class ReceivedMsgViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessage, tvDateTime;
        private final ShapeableImageView imgAvatar, imgMsg;

        public ReceivedMsgViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDateTime = itemView.findViewById(R.id.tvTime);
            imgAvatar = itemView.findViewById(R.id.img_avt);
            imgMsg = itemView.findViewById(R.id.img_msg);

        }

        void setData(MessageModel dataMsg, Context context, IAction iActionMessage, String urlAvatarUserFocus) {
            if (dataMsg != null) {
                Glide.with(context)
                        .load(urlAvatarUserFocus)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(imgAvatar);
                int msgType = dataMsg.getMessage_type();
                if (msgType == TYPE_MESSAGE.TEXT.getValue()) {
                    tvMessage.setText(dataMsg.getMessage());
                } else if (msgType == TYPE_MESSAGE.IMAGE.getValue()) {
                    Glide.with(context)
                            .load(dataMsg.getMessage())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(imgMsg);
                }
                String dataTime = dataMsg.getCreated_at();
                dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
                tvDateTime.setText(dataTime);


                itemView.setOnClickListener(v -> iActionMessage.onClick(dataMsg));
            }
        }
    }
}
