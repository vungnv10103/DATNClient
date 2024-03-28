package com.datn.client.models;

import java.util.List;

public class ProductOrder extends _BaseModel {

    private String order_id;
    private List<Product> products;
    private List<String> productsQuantity;
    private List<String> orderDetailID;
    private List<Integer> orderDetailStatus;
    private String amount;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<String> getProductsQuantity() {
        return productsQuantity;
    }

    public void setProductsQuantity(List<String> productsQuantity) {
        this.productsQuantity = productsQuantity;
    }

    public List<String> getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(List<String> orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public List<Integer> getOrderDetailStatus() {
        return orderDetailStatus;
    }

    public void setOrderDetailStatus(List<Integer> orderDetailStatus) {
        this.orderDetailStatus = orderDetailStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
