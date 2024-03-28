package com.datn.client.utils;

public enum STATUS_CART {
    DELETED(-1),
    DEFAULT(0),
    SELECTED(1),
    BOUGHT(2),
    BUYING(3);
    private final int value;

    STATUS_CART(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
