package com.example.socialmediaapp.signupfeature;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SignUpApi {
    @Headers({"Content-type: application/json"})
    @POST("account/signup")
    Call<Void> sendPost(@Body SignUpBody signInBody);

    @Headers({"Content-type: application/json"})
    @GET("interest/getAll")
    Call<List<InterestModel>> getInterests();
}
