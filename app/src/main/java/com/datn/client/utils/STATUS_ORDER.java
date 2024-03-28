package com.datn.client.utils;

public enum STATUS_ORDER {
    WAIT_CONFIRM(0),
    PREPARE(1),
    IN_TRANSIT(2),
    PAID(3),
    CANCEL(4);
    private final int value;

    STATUS_ORDER(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
