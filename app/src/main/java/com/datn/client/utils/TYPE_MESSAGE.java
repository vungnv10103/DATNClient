package com.datn.client.utils;

public enum TYPE_MESSAGE {
    IMAGE(0),
    TEXT(1),
    VIDEO(2),
    FILE(3);
    private final int value;

    TYPE_MESSAGE(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
