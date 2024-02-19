package com.datn.client.ui;

import android.app.AlertDialog;
import android.content.Context;

import com.datn.client.R;


public class MyDialog {
    private static MyDialog instance;

    public static MyDialog gI() {
        if (instance == null) {
            instance = new MyDialog();
        }
        return instance;
    }

    public void startDlgOK(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.notifications))
                .setMessage(message)
                .setIcon(R.drawable.logo)
                .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> dialog.dismiss())
                .show();
    }
}
