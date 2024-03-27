package com.datn.client.models;

import androidx.annotation.NonNull;

public class Order extends _BaseModel {
    private String customer_id;
    private String employee_id;
    private String delivery_address_id;
    private int status;
    private String amount;
    private int payment_method;
    private String guest_name;
    private String guest_phoneNumber;
    private String guest_address;

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getDelivery_address_id() {
        return delivery_address_id;
    }

    public void setDelivery_address_id(String delivery_address_id) {
        this.delivery_address_id = delivery_address_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(int payment_method) {
        this.payment_method = payment_method;
    }

    public String getGuest_name() {
        return guest_name;
    }

    public void setGuest_name(String guest_name) {
        this.guest_name = guest_name;
    }

    public String getGuest_phoneNumber() {
        return guest_phoneNumber;
    }

    public void setGuest_phoneNumber(String guest_phoneNumber) {
        this.guest_phoneNumber = guest_phoneNumber;
    }

    public String getGuest_address() {
        return guest_address;
    }

    public void setGuest_address(String guest_address) {
        this.guest_address = guest_address;
    }

    @NonNull
    @Override
    public String toString() {
        return "Order{" +
                "customer_id='" + customer_id + '\'' +
                ", employee_id='" + employee_id + '\'' +
                ", delivery_address_id='" + delivery_address_id + '\'' +
                ", status=" + status +
                ", amount='" + amount + '\'' +
                ", payment_method=" + payment_method +
                ", guest_name='" + guest_name + '\'' +
                ", guest_phoneNumber='" + guest_phoneNumber + '\'' +
                ", guest_address='" + guest_address + '\'' +
                '}';
    }
}
