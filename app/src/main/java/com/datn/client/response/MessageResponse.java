package com.datn.client.response;

import com.datn.client.models.MessageModel;

import java.util.List;

public class MessageResponse extends _BaseResponse {
    private List<MessageModel> messages;

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }
}
