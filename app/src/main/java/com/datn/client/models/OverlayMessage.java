package com.datn.client.models;

import android.view.View;

import java.util.List;

public class OverlayMessage extends _BaseModel {
    private List<String> customer_id;
    private int status;
    private String notification;
    private List<String> colors_gradient;
    private String image;
    private String title_image;
    private String content_image;
    private String title;
    private String content;

    private String text_action;
    private String color_action;
    private View.OnClickListener action;

    public OverlayMessage(List<String> customer_id, int status, String notification, List<String> colors_gradient,
                          String image, String title_image, String content_image, String title,
                          String content, String text_action, String color_action, View.OnClickListener action) {
        this.customer_id = customer_id;
        this.status = status;
        this.notification = notification;
        this.colors_gradient = colors_gradient;
        this.image = image;
        this.title_image = title_image;
        this.content_image = content_image;
        this.title = title;
        this.content = content;
        this.text_action = text_action;
        this.color_action = color_action;
        this.action = action;
    }

    public List<String> getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(List<String> customer_id) {
        this.customer_id = customer_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public List<String> getColors_gradient() {
        return colors_gradient;
    }

    public void setColors_gradient(List<String> colors_gradient) {
        this.colors_gradient = colors_gradient;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle_image() {
        return title_image;
    }

    public void setTitle_image(String title_image) {
        this.title_image = title_image;
    }

    public String getContent_image() {
        return content_image;
    }

    public void setContent_image(String content_image) {
        this.content_image = content_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getText_action() {
        return text_action;
    }

    public void setText_action(String text_action) {
        this.text_action = text_action;
    }

    public String getColor_action() {
        return color_action;
    }

    public void setColor_action(String color_action) {
        this.color_action = color_action;
    }

    public View.OnClickListener getAction() {
        return action;
    }

    public void setAction(View.OnClickListener action) {
        this.action = action;
    }
}
