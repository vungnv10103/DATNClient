package com.datn.client.services;

import androidx.annotation.NonNull;

import com.datn.client.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {
    @NonNull
    public static ApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.URL_API)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ApiService.class);
    }
}
