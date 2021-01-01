package com.example.socialmediaapp.posts;

import com.example.socialmediaapp.posts.models.PostDetailsModel;
import com.example.socialmediaapp.posts.models.PostModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostAPI {

    @GET("post/getAccountPosts/{id}")
    Call<List<PostModel>> getAccountPosts(@Header("Authorization") String token, @Path("id") int id, @Query("page") int page);

    @GET("post/get/{id}")
    Call<PostDetailsModel> getPostDetails(@Header("Authorization") String token, @Path("id") int id);

    @POST("post/add")
    Call<Void> addPost(@Header("Authorization") String token, @Body PostModel postModel);

    @PUT("post/edit/{id}")
    Call<Void> editPost(@Header("Authorization") String token, @Path("id") int id, @Body PostModel postModel);

    @DELETE("post/delete/{id}")
    Call<Void> deletePost(@Header("Authorization") String token, @Path("id") int id);
}
