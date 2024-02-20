package com.datn.client.services;

import com.datn.client.models.Customer;
import com.datn.client.response.BaseResponse;
import com.datn.client.response.VerifyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/v1/api/customer/register")
    Call<BaseResponse> registerCustomer(@Body Customer customer);

    @POST("/v1/api/customer/login")
    Call<BaseResponse> loginCustomer(@Body Customer customer);

    @POST("/v1/api/customer/login/verify")
    Call<VerifyResponse> verify(@Body Customer customer);

}
