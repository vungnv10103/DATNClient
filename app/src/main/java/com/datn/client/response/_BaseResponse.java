package com.datn.client.response;

import com.datn.client.models.MessageDetailResponse;

public class _BaseResponse {
    private MessageDetailResponse message;
    private int statusCode;
    private String code;
    private String timestamp;

    public MessageDetailResponse getMessage() {
        return message;
    }

    public void setMessage(MessageDetailResponse message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
