package com.datn.client.models;

public class Category extends _BaseModel {
    private String name;
    private String image;

    public Category(String _id, String name, String created_at, String image) {
        super(_id, created_at);
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
