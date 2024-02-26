package com.datn.client.models;

public class ProductCart extends _BaseModel {

    private String name;
    private String image;
    private String price;
    private String quantity_product;
    private String quantity_cart;

    private String note;
    private String status_product;
    private int status_cart;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getQuantity_product() {
        return quantity_product;
    }

    public void setQuantity_product(String quantity_product) {
        this.quantity_product = quantity_product;
    }

    public String getQuantity_cart() {
        return quantity_cart;
    }

    public void setQuantity_cart(String quantity_cart) {
        this.quantity_cart = quantity_cart;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus_product() {
        return status_product;
    }

    public void setStatus_product(String status_product) {
        this.status_product = status_product;
    }

    public int getStatus_cart() {
        return status_cart;
    }

    public void setStatus_cart(int status_cart) {
        this.status_cart = status_cart;
    }
}
