package com.datn.client.utils;

public enum STATUS_NOTIFICATION {
    DELETED(-1),
    SEEN(0),
    DEFAULT(1);
    private final int value;

    STATUS_NOTIFICATION(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
