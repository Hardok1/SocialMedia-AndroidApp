package com.example.socialmediaapp.relationships;

import com.example.socialmediaapp.profiles.models.PublicAccountInfoModel;
import com.example.socialmediaapp.relationships.models.RelationshipStatusModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RelationshipAPI {
    @POST("relationship/sendRequest/{id}")
    Call<Void> sendFriendRequest(@Header("Authorization") String token, @Path("id") int id);

    @POST("relationship/acceptRequestByUser/{id}")
    Call<Void> acceptFriendRequestWithUser(@Header("Authorization") String token, @Path("id") int id);

    @DELETE("relationship/removeRelationshipWithUser/{id}")
    Call<Void> removeRelationshipWithUser(@Header("Authorization") String token, @Path("id") int id);

    @GET("relationship/getRelationshipStatus/{id}")
    Call<RelationshipStatusModel> getRelationshipStatus(@Header("Authorization") String token, @Path("id") int id);

    @GET("relationship/getFriendsList")
    Call<List<PublicAccountInfoModel>> getFriendsList(@Header("Authorization") String token);

    @GET("relationship/getReceivedRequests")
    Call<List<PublicAccountInfoModel>> getReceivedRequests(@Header("Authorization") String token);

    @GET("relationship/getSentRequests")
    Call<List<PublicAccountInfoModel>> getSentRequests(@Header("Authorization") String token);


}
