package com.datn.client.utils;

public enum STATUS_MESSAGE {
    SENDING(0),
    SENT(1),
    RECEIVED(2),
    SEEN(3);
    private final int value;

    STATUS_MESSAGE(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
