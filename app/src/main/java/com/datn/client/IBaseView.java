package com.datn.client;

import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;

import java.util.List;

public interface IBaseView {
    void onListNotification(List<Notification> notificationList);

    void onListOverlayMessage(List<OverlayMessage> overlayMessages);

    void onThrowMessage(MessageDetailResponse message);

    void onThrowNotification(String notification);

    void onThrowLog(String key, String message);

    void onFinish();
}