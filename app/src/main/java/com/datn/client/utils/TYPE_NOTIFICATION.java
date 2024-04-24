package com.datn.client.utils;

public enum TYPE_NOTIFICATION {
    DEFAULT(-2),
    MESSAGE(-3);
    private final int value;

    TYPE_NOTIFICATION(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
