package com.example.socialmediaapp.comments;

import com.example.socialmediaapp.comments.models.CommentModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentAPI {

    @POST("comment/add/{id}")
    Call<Void> addComment(@Header("Authorization") String token, @Path("id") int id, @Body CommentModel commentModel);

    @PUT("comment/edit/{id}")
    Call<Void> editComment(@Header("Authorization") String token, @Path("id") int id, @Body CommentModel commentModel);

    @DELETE("comment/delete/{id}")
    Call<Void> deleteComment(@Header("Authorization") String token, @Path("id") int id);
}
