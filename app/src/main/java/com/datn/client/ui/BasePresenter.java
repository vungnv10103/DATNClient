package com.datn.client.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.response.NotificationResponse;
import com.datn.client.response.OverlayMessageResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasePresenter {
    private final FragmentActivity context;

    private final IBaseView iBaseView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<NotificationResponse> getNotification;
    private Call<NotificationResponse> updateStatusNotification;
    private Call<OverlayMessageResponse> getOverlayMessage;
    private Call<_BaseResponse> updateStatusOverlayMessage;


    public BasePresenter(FragmentActivity context, IBaseView iBaseView, ApiService apiService, String token, String customerID) {
        this.context = context;
        this.iBaseView = iBaseView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    public void onCancelAPI() {
        if (getNotification != null) {
            getNotification.cancel();
        }
        if (updateStatusNotification != null) {
            updateStatusNotification.cancel();
        }
        if (getOverlayMessage != null) {
            getOverlayMessage.cancel();
        }
        if (updateStatusOverlayMessage != null) {
            updateStatusOverlayMessage.cancel();
        }
    }

    public void getNotification() {
        context.runOnUiThread(() -> {
            try {
                getNotification = apiService.getNotification(token, customerID);
                getNotification.enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iBaseView.onThrowLog("getNotification200", code);
                                List<Notification> data = response.body().getNotifications();
                                iBaseView.onListNotification(data);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iBaseView.onFinish();
                                } else {
                                    iBaseView.onThrowLog("getNotification400", code);
                                    iBaseView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iBaseView.onThrowLog("getNotification: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                        iBaseView.onThrowLog("getNotification: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iBaseView.onThrowLog("getNotification", e.getMessage());
            }
        });
    }

    public void updateStatusNotification(String notificationID, int status) {
        context.runOnUiThread(() -> {
            try {
                updateStatusNotification = apiService.updateStatusNotification(token, customerID, notificationID, status);
                updateStatusNotification.enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iBaseView.onThrowLog("updateStatusNotification200", code);
                                List<Notification> data = response.body().getNotifications();
                                iBaseView.onListNotification(data);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iBaseView.onFinish();
                                } else {
                                    iBaseView.onThrowLog("updateStatusNotification400", code);
                                    iBaseView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iBaseView.onThrowLog("updateStatusNotification: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                        iBaseView.onThrowLog("updateStatusNotification: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iBaseView.onThrowLog("updateStatusNotification", e.getMessage());
            }
        });
    }


    public void getOverlayMessage() {
        context.runOnUiThread(() -> {
            try {
                getOverlayMessage = apiService.getOverlayMessage(token, customerID);
                getOverlayMessage.enqueue(new Callback<OverlayMessageResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OverlayMessageResponse> call, @NonNull Response<OverlayMessageResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iBaseView.onThrowLog("getOverlayMessage200", code);
                                List<OverlayMessage> data = response.body().getOverlayMessages();
                                iBaseView.onListOverlayMessage(data);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iBaseView.onFinish();
                                } else {
                                    iBaseView.onThrowLog("getOverlayMessage400", code);
                                    iBaseView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iBaseView.onThrowLog("getOverlayMessage: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OverlayMessageResponse> call, @NonNull Throwable t) {
                        iBaseView.onThrowLog("getOverlayMessage: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iBaseView.onThrowLog("getOverlayMessage", e.getMessage());
            }
        });
    }

    public void updateStatusOverlayMessage(String overlayMessageID) {
        context.runOnUiThread(() -> {
            try {
                updateStatusOverlayMessage = apiService.updateStatusOverlayMessage(token, customerID, overlayMessageID);
                updateStatusOverlayMessage.enqueue(new Callback<_BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                iBaseView.onThrowLog("updateStatusOverlayMessage200", code);
                                iBaseView.onThrowMessage(message);
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iBaseView.onFinish();
                                } else {
                                    iBaseView.onThrowLog("updateStatusOverlayMessage400", code);
                                    iBaseView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iBaseView.onThrowLog("getOverlayMessage: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                        iBaseView.onThrowLog("updateStatusOverlayMessage: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iBaseView.onThrowLog("updateStatusOverlayMessage", e.getMessage());
            }
        });
    }

    public enum STATUS_NOTIFICATION {
        DELETED(-1),
        SEEN(0),
        DEFAULT(1);
        private final int value;

        STATUS_NOTIFICATION(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }
}
