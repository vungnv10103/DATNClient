package com.datn.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.Customer;
import com.datn.client.models.UserModel;
import com.datn.client.models._BaseModel;
import com.datn.client.response.ConversationDisplay;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.MyFormat;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private final List<ConversationDisplay> dataConversation;
    private final Customer customerLogged;
    private final Context context;
    private final IAction iActionConversation;
    private final Date currentTime = Calendar.getInstance().getTime();
    public static HashMap<String, UserModel> dataUsersFocus;

    public ConversationAdapter(Context context, List<ConversationDisplay> dataConversation, IAction iActionConversation) {
        this.context = context;
        dataUsersFocus = new HashMap<>();
        customerLogged = ManagerUser.gI().getCustomerLogin((FragmentActivity) context);
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
        ConversationDisplay conversation = dataConversation.get(position);
        List<UserModel> members = conversation.getMetadata();
        String userIDLogged = customerLogged.get_id();
        String prefixMsg = userIDLogged.equals(conversation.getSender_id()) ? "Bạn: " : "";
        String imgAvatar;
        if (members.size() == 2) {
            UserModel dataUserFocus = getDataUserFocus(members, userIDLogged);
            if (dataUserFocus == null) {
                return;
            }
            dataUsersFocus.put(conversation.getConversation_id(), dataUserFocus);
            imgAvatar = dataUserFocus.getAvatar();
            holder.tvNameUser.setText(dataUserFocus.getFull_name());
        } else {
            imgAvatar = "https://stech-993p.onrender.com/images/logo.jpeg";
            holder.tvNameUser.setText(conversation.getConversation_name());
        }
        Glide.with(context)
                .load(imgAvatar)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgAvt);
        if (!conversation.getMessage().isEmpty()) {
            holder.tvLastMessage.setText(String.format(prefixMsg + conversation.getMessage()));
        }

        if (conversation.getCreated_at().length() >= 16) {
            holder.tvTime.setText(MyFormat.formatTime(context, conversation.getCreated_at(), currentTime));
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
        private final TextView tvNameUser, tvLastMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.img_avt);
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

    public void updateList(List<ConversationDisplay> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ConversationAdapter.ConversationDiffCallback(newList, dataConversation));
        int oldSize = dataConversation.size();
        dataConversation.clear();
        dataConversation.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        int newSize = newList.size();
        System.out.println("oldSize: " + oldSize + " - newSize:" + newSize);
    }

    private static class ConversationDiffCallback extends DiffUtil.Callback {
        private final List<ConversationDisplay> oldList;
        private final List<ConversationDisplay> newList;

        public ConversationDiffCallback(List<ConversationDisplay> newList, List<ConversationDisplay> oldList) {
            this.newList = newList;
            this.oldList = oldList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldList.get(oldItemPosition).get_id(), newList.get(newItemPosition).get_id());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ConversationDisplay oldConversation = oldList.get(oldItemPosition);
            ConversationDisplay newConversation = newList.get(newItemPosition);
            return oldConversation.getConversation_id().equals(newConversation.getConversation_id())
                    && Objects.equals(oldConversation.getConversation_name(), newConversation.getConversation_name())
                    && Objects.equals(oldConversation.getMessage(), newConversation.getMessage());
        }
    }
}
