package com.datn.client.models;

import androidx.annotation.NonNull;

public class MessageResponse extends _BaseModel {
    private int code;
    private String title;
    private String content;
    private String image;

    public MessageResponse(String _id, String created_at, int code, String title, String content, String image) {
        super(_id, created_at);
        this.code = code;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    @NonNull
    @Override
    public String toString() {
        return "MessageResponse{" +
                "code=" + code +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
