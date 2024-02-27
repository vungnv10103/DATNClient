package com.datn.client.response;

import java.util.HashMap;

public class PaymentMethodResponse extends _BaseResponse {
    private HashMap<Integer, String> paymentMethod;

    public HashMap<Integer, String> getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(HashMap<Integer, String> paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
