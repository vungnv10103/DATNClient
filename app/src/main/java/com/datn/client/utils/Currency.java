package com.datn.client.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Currency {
    public static String formatCurrency(String amount) {
        try {
            long number = Long.parseLong(amount);
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
            formatSymbols.setGroupingSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#,###,###.###", formatSymbols);
            return decimalFormat.format(number) + " VND";
        } catch (NumberFormatException e) {
            return amount;
        }
    }
}
