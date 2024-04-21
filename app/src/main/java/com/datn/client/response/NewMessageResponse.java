package com.datn.client.response;

import com.datn.client.models.MessageModel;

public class NewMessageResponse extends _BaseResponse {
    private MessageModel newMessage;

    public MessageModel getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(MessageModel newMessage) {
        this.newMessage = newMessage;
    }
}
