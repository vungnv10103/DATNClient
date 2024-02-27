package com.datn.client.services;

import com.datn.client.models.Cart;
import com.datn.client.models.Customer;
import com.datn.client.response.BannerResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.response.CategoryResponse;
import com.datn.client.response.CustomerResponse;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.response.ProductResponse;

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
    Call<_BaseResponse> checkLogin(@Body Customer customer);

    @POST("/v1/api/customer/login/verify")
    Call<CustomerResponse> verify(@Body Customer customer);

    @POST("/v1/api/customer/add/fcm")
    Call<_BaseResponse> addFCM(@Header("Authorization") String token, @Body Customer customer);


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
    @POST("/v1/api/product/get/category")
    Call<ProductResponse> getProductByCateID(@Header("Authorization") String token,
                                             @Field("customerID") String customerID,
                                             @Field("categoryID") String categoryID);

    @FormUrlEncoded
    @POST("/v1/api/product/detail")
    Call<ProductResponse> getDetailProduct(@Header("Authorization") String token,
                                           @Field("customerID") String customerID,
                                           @Field("productID") String productID);

    @FormUrlEncoded
    @POST("/v1/api/cart/get/customer")
    Call<ProductCartResponse> getCart(@Header("Authorization") String token, @Field("customerID") String customerID);

    @POST("/v1/api/cart/add")
    Call<_BaseResponse> addToCart(@Header("Authorization") String token, @Body Cart cart);

    @FormUrlEncoded
    @POST("/v1/api/cart/update/quantity")
    Call<ProductCartResponse> updateQuantity(@Header("Authorization") String token,
                                             @Field("customerID") String customerID,
                                             @Field("type") String type,
                                             @Field("quantity") int quantity,
                                             @Field("cartID") String cartID);

    @FormUrlEncoded
    @POST("/v1/api/cart/update/status")
    Call<ProductCartResponse> updateStatus(@Header("Authorization") String token,
                                           @Field("customerID") String customerID,
                                           @Field("status") int status,
                                           @Field("cartID") String cartID);

    @FormUrlEncoded
    @POST("/v1/api/cart/update/status-all")
    Call<ProductCartResponse> updateStatusAll(@Header("Authorization") String token,
                                              @Field("customerID") String customerID,
                                              @Field("isSelected") boolean isSelected);

    @FormUrlEncoded
    @POST("/v1/api/checkout/get/customer")
    Call<ProductCartResponse> getProductCheckout(@Header("Authorization") String token,
                                                 @Field("customerID") String customerID);

}
