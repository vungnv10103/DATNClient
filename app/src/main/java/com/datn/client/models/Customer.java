package com.datn.client.models;

public class Customer extends _BaseModel {
    private String avatar;
    private String email;
    private String password;
    private String full_name;
    private String phone_number;
    private String status;
    private String otp;
    private String fcm;



    public Customer(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Customer(String _id, String password, boolean _is) {
        super(_id);
        this.password = password;
    }

    public Customer(String email, String password, String full_name, String phone_number) {
        this.email = email;
        this.password = password;
        this.full_name = full_name;
        this.phone_number = phone_number;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getFcm() {
        return fcm;
    }

    public void setFcm(String fcm) {
        this.fcm = fcm;
    }
}
