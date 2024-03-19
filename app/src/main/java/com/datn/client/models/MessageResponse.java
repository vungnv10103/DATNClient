package com.datn.client.models;

import androidx.annotation.NonNull;

public class MessageResponse extends _BaseModel {
    private int statusCode;
    private String code;
    private String title;
    private String content;
    private String image;

    public MessageResponse(String _id, String created_at, int statusCode, String code, String title, String content, String image) {
        super(_id, created_at);
        this.statusCode = statusCode;
        this.code = code;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
