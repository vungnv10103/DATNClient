package com.datn.client.ui.dashboard;

import com.datn.client.models.MessageResponse;

public interface IDashboardView {

    void onThrowMessage(MessageResponse message);
    void onThrowMessage(String message);
    void onLogout();
}
