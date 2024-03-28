package com.datn.client.utils;

public enum STATUS_PRODUCT {
    OUT_OF_STOCK(0),
    STOCKING(1);
    private final int value;

    STATUS_PRODUCT(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
