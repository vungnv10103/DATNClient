package com.datn.client.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.MessageDetailResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardPresenter extends BasePresenter {
    private final FragmentActivity context;
    private final IDashboardView iDashboardView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<_BaseResponse> logout;

    public DashboardPresenter(FragmentActivity context, IDashboardView iDashboardView, ApiService apiService, String token, String customerID) {
        super(context, iDashboardView, apiService, token, customerID);
        this.context = context;
        this.iDashboardView = iDashboardView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void onCancelAPI() {
        super.onCancelAPI();
        if (logout != null) {
            logout.cancel();
        }

    }

    public void logout() {
        context.runOnUiThread(() -> {
            try {
                logout = apiService.logout(token, customerID);
                logout.enqueue(new Callback<_BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iDashboardView.onThrowLog("logout: onResponse200", code);
                                iDashboardView.onLogout();
                            } else if (statusCode == 400) {
                                iDashboardView.onThrowLog("logout: onResponse400", code);
                                if (code.equals("auth/wrong-token")) {
                                    iDashboardView.onLogout();
                                } else {
                                    iDashboardView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iDashboardView.onThrowNotification("logout: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                        iDashboardView.onThrowNotification("logout: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iDashboardView.onThrowNotification("logout: " + e.getMessage());
            }
        });
    }
}
