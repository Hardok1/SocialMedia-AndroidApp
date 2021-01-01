package com.example.socialmediaapp.signinfeature;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SignInApi {
    @Headers({"Content-type: application/json"})
    @POST("login")
    Call<Void> sendPost(@Body SignInBody signInBody);

    @GET("account/getId")
    Call<String> getAccountId(@Header("Authorization") String token);


}
