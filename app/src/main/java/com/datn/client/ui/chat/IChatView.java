package com.datn.client.ui.chat;

import com.datn.client.IBaseView;
import com.datn.client.models.MessageModel;
import com.datn.client.response.ConversationDisplay;

import java.util.List;

public interface IChatView extends IBaseView {
    void onListConversation(List<ConversationDisplay> dataConversation);

    void onDataMessage(List<MessageModel> dataMessage);

    void onNewMessage(MessageModel newMessage);
}
