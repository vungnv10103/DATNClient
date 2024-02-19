package com.datn.client.services;

import com.datn.client.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {
    public static ApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.URL_API)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ApiService.class);
    }
}
