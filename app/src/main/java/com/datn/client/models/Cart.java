package com.datn.client.models;

public class Cart extends _BaseModel {
    private String customer_id;
    private String product_id;
    private int quantity;
    private int status;
    private String note;

    public Cart(String customer_id, String product_id, int quantity, int status, String note) {
        this.customer_id = customer_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.status = status; // default = 1
        this.note = note;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
