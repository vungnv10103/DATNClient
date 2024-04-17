package com.datn.client.ui.chat;

import com.datn.client.response.Demo;
import com.datn.client.ui.IBaseView;

import java.util.List;

public interface IChatView extends IBaseView {
    void onListConversation(List<Demo> dataConversation);
}
