package com.datn.client.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Customer;
import com.datn.client.models.UserModel;
import com.datn.client.models._BaseModel;
import com.datn.client.response.Demo;
import com.datn.client.utils.ManagerUser;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private final List<Demo> dataConversation;
    private final Customer customerLogged;
    private final Context context;
    private final IAction iActionConversation;


    public ConversationAdapter(Context context, List<Demo> dataConversation, IAction iActionConversation) {
        this.context = context;
        customerLogged = ManagerUser.gI().checkCustomer((FragmentActivity) context);
        this.dataConversation = dataConversation;
        this.iActionConversation = iActionConversation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Demo conversation = dataConversation.get(position);
        List<UserModel> members = conversation.getMetadata();
        String userIDLogged = customerLogged.get_id();
        String prefixMsg = userIDLogged.equals(conversation.getSender_id()) ? "Bạn: " : "";
        String imgAvatar;
        if (members.size() == 2) {
            UserModel dataUserFocus = getDataUserFocus(members, userIDLogged);
            if (dataUserFocus == null) {
                return;
            }
            imgAvatar = dataUserFocus.getAvatar();
            holder.tvNameUser.setText(dataUserFocus.getFull_name());
        } else {
            imgAvatar = "https://stech-993p.onrender.com/images/logo.jpeg";
            holder.tvNameUser.setText(conversation.getConversation_name());
        }
        Glide.with(context)
                .load(imgAvatar)
                .error(R.drawable.logo_app_gradient)
                .into(holder.imgAvt);
        if (!conversation.getMessage().isEmpty()) {
            holder.tvLastMessage.setText(String.format(prefixMsg + conversation.getMessage()));
        }

        if (conversation.getCreated_at().length() >= 16) {
            holder.tvTime.setText(conversation.getCreated_at().substring(11, 16));
        }

        holder.itemView.setOnClickListener(v -> {
            _BaseModel con = new _BaseModel(conversation.getConversation_id(), conversation.getCreated_at());
            iActionConversation.onClick(con);
        });


    }

    @Override
    public int getItemCount() {
        if (dataConversation != null) {
            return dataConversation.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView imgAvt;
        private final RelativeLayout layoutMsg;
        private final TextView tvNameUser, tvLastMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.img_avt);
            layoutMsg = itemView.findViewById(R.id.layout_msg);
            tvNameUser = itemView.findViewById(R.id.tv_nameUser);
            tvLastMessage = itemView.findViewById(R.id.tv_lastMessage);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    @Nullable
    private static UserModel getDataUserFocus(@NonNull List<UserModel> members, String userIDLogged) {
        for (UserModel member : members) {
            if (!member.getUser_id().equals(userIDLogged)) {
                return member;
            }
        }
        return null;
    }
}
