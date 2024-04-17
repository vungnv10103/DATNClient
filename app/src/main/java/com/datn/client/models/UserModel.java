package com.datn.client.models;

public class UserModel extends _BaseModel {
    private String user_id;
    private String email;
    private String avatar;
    private String full_name;
    private String phone_number;

    public UserModel(String user_id, String email, String avatar, String full_name, String phone_number) {
        this.user_id = user_id;
        this.email = email;
        this.avatar = avatar;
        this.full_name = full_name;
        this.phone_number = phone_number;
    }

    public UserModel(String email) {
        this.email = email;
    }

    public UserModel(String user_id, boolean _is) {
        this.user_id = user_id;
    }

    public UserModel(String email, String full_name, String phone_number) {
        this.email = email;
        this.full_name = full_name;
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
