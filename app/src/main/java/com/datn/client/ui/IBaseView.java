package com.datn.client.ui;

import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;

import java.util.List;

public interface IBaseView {
    void onListNotification(List<Notification> notificationList);

    void onListOverlayMessage(List<OverlayMessage> overlayMessages);

    void onThrowMessage(MessageResponse message);

    void onThrowLog(String key, String message);

    void onFinish();
}