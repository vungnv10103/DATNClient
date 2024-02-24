package com.datn.client.services;

import com.datn.client.models.Customer;
import com.datn.client.response.BannerResponse;
import com.datn.client.response.BaseResponse;
import com.datn.client.response.CategoryResponse;
import com.datn.client.response.CustomerResponse;
import com.datn.client.response.ProductResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/v1/api/customer/register")
    Call<CustomerResponse> registerCustomer(@Body Customer customer);

    @POST("/v1/api/customer/login")
    Call<CustomerResponse> loginCustomer(@Body Customer customer);

    @POST("/v1/api/customer/login/check")
    Call<BaseResponse> checkLogin(@Body Customer customer);

    @POST("/v1/api/customer/login/verify")
    Call<CustomerResponse> verify(@Body Customer customer);

    @POST("/v1/api/customer/add/fcm")
    Call<BaseResponse> addFCM(@Header("Authorization") String token, @Body Customer customer);


    @FormUrlEncoded
    @POST("/v1/api/banner/get")
    Call<BannerResponse> getBanner(@Header("Authorization") String token, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/category/get")
    Call<CategoryResponse> getCategory(@Header("Authorization") String token, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/product/get")
    Call<ProductResponse> getSellingProduct(@Header("Authorization") String token, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/product/detail")
    Call<ProductResponse> getDetailProduct(@Header("Authorization") String token,
                                           @Field("customerID") String customerID,
                                           @Field("productID") String productID);

    @FormUrlEncoded
    @POST("/v1/api/cart/get")
    Call<ProductResponse> getCart(@Header("Authorization") String token, @Field("customerID") String customerID);

}
