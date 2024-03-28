package com.datn.client.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import com.datn.client.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFormat {

    public static String compareTime(Context context, String timeReceive, Date currentTime, boolean isShowSelected) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        try {
            Date dateReceive = format.parse(timeReceive);
            if (dateReceive != null && currentTime != null) {
                long timeBetween = Math.abs(dateReceive.getTime() - currentTime.getTime());
                long day = timeBetween / (24 * 60 * 60 * 1000);
                long hour = timeBetween / (60 * 60 * 1000);
                long minutes = timeBetween / (60 * 1000);
                long second = timeBetween / 1000;
                if (day >= 1) {
                    if (isShowSelected) {
                        return day + context.getString(R.string.des_short_day_time);
                    }
                    return day + context.getString(R.string.des_long_day_time);
                } else if (hour >= 1) {
                    if (isShowSelected) {
                        return hour + context.getString(R.string.des_short_hour_time);
                    }
                    return hour + context.getString(R.string.des_long_hour_time);
                } else if (minutes >= 1) {
                    if (isShowSelected) {
                        return minutes + context.getString(R.string.des_short_minute_time);
                    }
                    return minutes + context.getString(R.string.des_long_minute_time);
                } else {
                    if (isShowSelected) {
                        return second + context.getString(R.string.des_short_second_time);
                    }
                    return second + context.getString(R.string.des_long_second_time);
                }
            }
            return timeReceive;
        } catch (ParseException e) {
            Log.d("NotificationAdapter", "compareTime: " + e.getMessage());
            return timeReceive;
        }
    }

    public static String formatCurrency(String amount) {
        try {
            long number = Long.parseLong(amount);
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
            formatSymbols.setGroupingSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#,###,###.###", formatSymbols);
            return decimalFormat.format(number) + " VND";
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
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

    public static int pxToDp(@NonNull Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
