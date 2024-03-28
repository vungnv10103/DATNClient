package com.datn.client.utils;

import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.Customer;
import com.google.gson.Gson;

public class ManagerUser {
    private PreferenceManager preferenceManager;

    private static ManagerUser instance;

    public static ManagerUser gI() {
        if (instance == null) {
            instance = new ManagerUser();
        }
        return instance;
    }

    public Customer checkCustomer(FragmentActivity context) {
        preferenceManager = new PreferenceManager(context, Constants.KEY_PREFERENCE_ACC);
        Gson gson = new Gson();
        String json = preferenceManager.getString(Constants.KEY_USER);
        return gson.fromJson(json, Customer.class);
    }

    public String checkToken(FragmentActivity context) {
        preferenceManager = new PreferenceManager(context, Constants.KEY_PREFERENCE_ACC);
        String mToken = preferenceManager.getString(Constants.KEY_TOKEN);
        if (mToken == null || mToken.isEmpty()) {
            return null;
        }
        return mToken;
    }
}
