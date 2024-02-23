package com.datn.client.models;

import androidx.annotation.NonNull;

public class Banner extends BaseModel {

    private String creator_id;
    private String url;


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


}
