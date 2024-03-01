package com.datn.client.response;

public class EBankingResponse extends _BaseResponse {
    private String paymentURL;

    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
    }
}
