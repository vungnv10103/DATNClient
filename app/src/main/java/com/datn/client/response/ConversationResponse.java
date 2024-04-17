package com.datn.client.response;

import com.datn.client.models.ConversationModel;

import java.util.List;

public class ConversationResponse extends _BaseResponse {
    private List<Demo> conversations;

    public List<Demo> getConversations() {
        return conversations;
    }

    public void setConversations(List<Demo> conversations) {
        this.conversations = conversations;
    }
}
