package com.datn.client.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MyFormat {
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

    public static int convertDPtoPx(@NonNull Context context, float dip) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }
}
