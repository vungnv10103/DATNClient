package com.datn.client.response;

import com.datn.client.models.Notification;

import java.util.List;

public class NotificationResponse extends _BaseResponse {
    private List<Notification> notifications;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
