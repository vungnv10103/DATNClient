package com.datn.client.ui.components;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.datn.client.R;
import com.datn.client.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class MyDialog {
    private static MyDialog instance;
    private final int iconID = Constants.isNightMode ? R.drawable.logo_app_white_no_bg : R.drawable.logo_app_gradient;

    public static MyDialog gI() {
        if (instance == null) {
            instance = new MyDialog();
        }
        return instance;
    }

    public void startDlgOK(Context context, String title, String message,
                           DialogInterface.OnClickListener negativeAction,
                           DialogInterface.OnClickListener positiveAction) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(iconID)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Decline", negativeAction)
                .setPositiveButton("Accept", positiveAction)
                .setCancelable(false)
                .show();

    }

    public void startDlgOKWithAction(Context context, String title, String message,
                                     DialogInterface.OnClickListener positiveAction) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(iconID)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, positiveAction)
                .setCancelable(false)
                .show();

    }
    public void startDlgOKWithAction(Context context, String title, String message,
                                     String titlePositive,
                                     DialogInterface.OnClickListener positiveAction) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(iconID)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(titlePositive, positiveAction)
                .setCancelable(false)
                .show();

    }

    public void startDlgOKWithAction(Context context, String title, String message,
                                     DialogInterface.OnClickListener positiveAction,
                                     DialogInterface.OnClickListener negativeAction) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(iconID)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, positiveAction)
                .setNegativeButton(android.R.string.no, negativeAction)
                .setCancelable(false)
                .show();

    }

    public void startDlgOKWithAction(Context context, String message, DialogInterface.OnClickListener action) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(iconID)
                .setTitle(context.getString(R.string.notifications))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, action)
                .setCancelable(false)
                .show();

    }

    public void startDlgOK(Context context, @NonNull Object message) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(iconID)
                .setTitle(context.getString(R.string.notifications))
                .setMessage(message.toString())
                .setPositiveButton(android.R.string.ok, ((dialog, which) -> dialog.dismiss()))
                .setCancelable(false)
                .show();

    }

    public void startDlgOK(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(iconID)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, ((dialog, which) -> dialog.dismiss()))
                .setCancelable(false)
                .show();
    }

}
