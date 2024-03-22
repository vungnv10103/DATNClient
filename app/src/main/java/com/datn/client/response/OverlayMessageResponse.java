package com.datn.client.response;

import com.datn.client.models.OverlayMessage;

import java.util.List;

public class OverlayMessageResponse extends _BaseResponse {
    private List<OverlayMessage> overlayMessages;

    public List<OverlayMessage> getOverlayMessages() {
        return overlayMessages;
    }

    public void setOverlayMessages(List<OverlayMessage> overlayMessages) {
        this.overlayMessages = overlayMessages;
    }
}
