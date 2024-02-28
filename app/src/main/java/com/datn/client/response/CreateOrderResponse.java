package com.datn.client.response;

public class CreateOrderResponse extends _BaseResponse{
    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
