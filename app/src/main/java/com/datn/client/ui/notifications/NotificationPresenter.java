package com.datn.client.ui.notifications;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.response.NotificationResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.BasePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationPresenter extends BasePresenter {

    private final FragmentActivity context;

    private final INotificationView iNotificationView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;


    public NotificationPresenter(FragmentActivity context, INotificationView iNotificationView, ApiService apiService, String token, String customerID) {
        super(context, iNotificationView, apiService, token, customerID);
        this.context = context;
        this.iNotificationView = iNotificationView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    @Override
    public void cancelAPI() {
        super.cancelAPI();
    }


}
