package com.datn.client.response;

import com.datn.client.models.Customer;

public class CustomerResponse extends _BaseResponse {
    private Customer customer;
    private String token;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
