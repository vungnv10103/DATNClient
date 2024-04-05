package com.datn.client.helper;

import android.app.Activity;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

public class MyNavigationBar {

    private static MyNavigationBar instance;

    public static MyNavigationBar gI() {
        if (instance == null) {
            instance = new MyNavigationBar();
        }
        return instance;
    }

    public int getNavigationBarHeight(@NonNull Activity view) {
        DisplayMetrics metrics = new DisplayMetrics();
        view.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        view.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}
