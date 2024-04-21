package com.datn.client.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MyPermission {
    public static final int REQUEST_CODE_OPEN_CAMERA = 1;
    public static final int REQUEST_CODE_APP_SETTINGS = 2;

    private static MyPermission instance;

    public static MyPermission gI() {
        if (instance == null) {
            return instance = new MyPermission();
        }
        return instance;
    }

    public boolean checkSelfPermission(Activity activity) {
        return (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        );
    }

    public void requestPermission(Activity context) {
        ActivityCompat.requestPermissions(
                context,
                new String[]{android.Manifest.permission.CAMERA},
                REQUEST_CODE_OPEN_CAMERA
        );
    }
}
