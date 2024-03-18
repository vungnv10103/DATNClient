package com.datn.client.ui.dashboard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.datn.client.models.Banner;
import com.datn.client.models.MessageResponse;
import com.datn.client.response.BannerResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.ui.home.HomePresenter;
import com.datn.client.ui.home.IHomeView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardPresenter {
    private static final String TAG = DashboardPresenter.class.getSimpleName();

    private final IDashboardView iDashboardView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<_BaseResponse> logout;

    public DashboardPresenter(IDashboardView iDashboardView, ApiService apiService, String token, String customerID) {
        this.iDashboardView = iDashboardView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void cancelAPI() {
        if (logout != null) {
            logout.cancel();
        }

    }

    public void logout() {
        try {

            logout = apiService.logout(token, customerID);
            logout.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                    if (response.body() != null) {
                        int statusCode = response.body().getStatusCode();
                        String code = response.body().getCode();
                        MessageResponse message = response.body().getMessage();
                        if (statusCode == 200) {
                            Log.w(TAG, "onResponse200: " + code);
                            iDashboardView.onLogout();
                        } else if (statusCode == 400) {
                            if (code.equals("auth/wrong-token")) {
                                iDashboardView.onFinish();
                            } else {
                                Log.w(TAG, "onResponse400: " + code);
                                iDashboardView.onThrowMessage(message);
                            }
                        }
                    } else {
                        Log.w(TAG, "onResponse: " + response.message());
                        iDashboardView.onThrowMessage(response.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                    iDashboardView.onThrowMessage("logout: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "logout: " + e.getMessage());
            iDashboardView.onThrowMessage(e.getMessage());
        }
    }
}
