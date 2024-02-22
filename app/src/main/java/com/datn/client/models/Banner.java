package com.datn.client.models;

import androidx.annotation.NonNull;

public class Banner {

    private String _id;
    private String creator_id;
    private String url;
    private String created_time;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    @NonNull
    @Override
    public String toString() {
        return "Banner{" +
                "_id='" + _id + '\'' +
                ", creator_id='" + creator_id + '\'' +
                ", url='" + url + '\'' +
                ", created_time='" + created_time + '\'' +
                '}';
    }
}
