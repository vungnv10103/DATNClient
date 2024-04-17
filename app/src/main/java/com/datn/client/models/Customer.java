package com.datn.client.models;

public class Customer extends UserModel {
    private String password;
    private String token;

    private String status;
    private String otp;
    private String fcm;


    public Customer(String email, String password) {
        super(email);
        this.password = password;
    }

    public Customer(String email, String password, String token) {
        super(email);
        this.password = password;
        this.token = token;
    }

    public Customer(String _id, String password, boolean _is) {
        super(_id, _is);
        this.password = password;
    }

    public Customer(String email, String password, String full_name, String phone_number) {
        super(email, full_name, phone_number);
        this.password = password;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
