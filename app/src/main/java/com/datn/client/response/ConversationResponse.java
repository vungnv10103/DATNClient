package com.datn.client.response;

import java.util.List;

public class ConversationResponse extends _BaseResponse {
    private List<ConversationDisplay> conversations;

    public List<ConversationDisplay> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationDisplay> conversations) {
        this.conversations = conversations;
    }
}
