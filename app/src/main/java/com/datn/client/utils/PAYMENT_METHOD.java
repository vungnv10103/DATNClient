package com.datn.client.utils;

public enum PAYMENT_METHOD {
    DELIVERY(0),
    E_BANKING(1),
    ZALO_PAY(2);
    private final int value;

    PAYMENT_METHOD(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
