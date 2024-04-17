package com.datn.client.models;

import java.util.List;

public class ConversationModel extends _BaseModel {
    private String title;
    private String creator_id;
    private List<String> member_id;
    private String updated_at;
    private String deleted_at;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public List<String> getMember_id() {
        return member_id;
    }

    public void setMember_id(List<String> member_id) {
        this.member_id = member_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
