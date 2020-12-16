package com.example.socialmediaapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.103:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit getRetrofit(){
        return retrofit;
    }
}
