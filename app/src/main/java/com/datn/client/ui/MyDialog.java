package com.datn.client.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;

import com.datn.client.R;


public class MyDialog {
    private static MyDialog instance;

    public static MyDialog gI() {
        if (instance == null) {
            instance = new MyDialog();
        }
        return instance;
    }

    private void startCountDown(long count, long step) {
        new CountDownTimer(count, step) {

            public void onTick(long millisUntilFinished) {
                System.out.println(millisUntilFinished / 1000);
            }

            public void onFinish() {
                System.out.println("done!");
            }

        }.start();
    }

    public void startDlgOK(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.notifications))
                .setMessage(message)
                .setIcon(R.drawable.logo)
                .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> dialog.dismiss())
                .show();
    }

    public void startDlgOK(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.logo)
                .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> dialog.dismiss())
                .show();
    }
}
