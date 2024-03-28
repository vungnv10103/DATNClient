package com.datn.client.utils;

public enum TYPE_BUY {
    BUY_NOW(0),
    ADD_TO_CART(1);
    private final int value;

    TYPE_BUY(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
