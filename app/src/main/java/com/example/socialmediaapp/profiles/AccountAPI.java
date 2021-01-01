package com.example.socialmediaapp.profiles;

import com.example.socialmediaapp.profiles.models.AccountDetailsModel;
import com.example.socialmediaapp.profiles.models.AccountModel;
import com.example.socialmediaapp.profiles.models.PublicAccountInfoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccountAPI {
    @DELETE("account/remove")
    Call<Void> deleteAccount(@Header("Authorization") String token);

    @PUT("account/edit")
    Call<Void> editAccount(@Header("Authorization") String token, @Body AccountModel accountModel);

    @GET("account/{id}")
    Call<AccountDetailsModel> getAccountDetails(@Header("Authorization") String token, @Path("id") int id);

    @GET("account/")
    Call<AccountDetailsModel> getMyAccountDetails(@Header("Authorization") String token);

    @GET("account/findByName")
    Call<List<PublicAccountInfoModel>> findByName(@Header("Authorization") String token, @Query("page") int page,
                                                  @Query("forename") String forename, @Query("surname") String surname);

    @GET("account/findByCity")
    Call<List<PublicAccountInfoModel>> findByCity(@Header("Authorization") String token, @Query("page") int page,
                                                  @Query("city") String city);

    @GET("account/findByCountry")
    Call<List<PublicAccountInfoModel>> findByCountry(@Header("Authorization") String token, @Query("page") int page,
                                                     @Query("country") String country);

    @GET("account/findByInterest")
    Call<List<PublicAccountInfoModel>> findByInterest(@Header("Authorization") String token, @Query("page") int page,
                                                      @Query("interest") String interest);

    @GET("account/findByInterestAndCity")
    Call<List<PublicAccountInfoModel>> findByInterestAndCity(@Header("Authorization") String token, @Query("page") int page,
                                                             @Query("interest") String interest, @Query("city") String city);

    @GET("account/findByInterestAndCountry")
    Call<List<PublicAccountInfoModel>> findByInterestAndCountry(@Header("Authorization") String token, @Query("page") int page,
                                                                @Query("interest") String interest, @Query("country") String country);

    @GET("account/isTokenActive")
    Call<Void> isTokenActive(@Header("Authorization") String token);
}
