package com.datn.client.ui;

import android.app.Activity;
import android.content.Context;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.datn.client.R;

public class MyNavController {
    private static MyNavController instance;

    public static MyNavController gI() {
        if (instance == null) {
            instance = new MyNavController();
        }
        return instance;
    }

    public void navigateFragment(Activity activity, int navigationID) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        navController.navigate(navigationID);
    }
}
