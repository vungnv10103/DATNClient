package com.datn.client.response;

import com.datn.client.models.UserModel;
import com.datn.client.models._BaseModel;

import java.util.List;

public class ConversationDisplay extends _BaseModel {
    private String conversation_id;
    private String conversation_name;
    private String sender_id;
    private List<UserModel> metadata;
    private String message;
    private int message_type;
    private int status;

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getConversation_name() {
        return conversation_name;
    }

    public void setConversation_name(String conversation_name) {
        this.conversation_name = conversation_name;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public List<UserModel> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<UserModel> metadata) {
        this.metadata = metadata;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
