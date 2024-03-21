package com.datn.client.utils;

import android.os.CountDownTimer;

public class MyCountDown {
    private static MyCountDown instance;

    public static MyCountDown gI() {
        if (instance == null) {
            instance = new MyCountDown();
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
}
