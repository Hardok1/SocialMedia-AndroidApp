package com.example.socialmediaapp.chat;

import com.example.socialmediaapp.chat.model.MessageModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatAPI {
    @POST("/chat/add/{id}")
    Call<Void> addChat(@Header("Authorization") String token, @Path("id") int id);

    @GET("/message/get/{id}")
    Call<List<MessageModel>> getMessages(@Header("Authorization") String token, @Path("id") int id);

    @POST("/message/add/{id}")
    Call<Void> addMessage(@Header("Authorization") String token, @Body MessageModel messageModel, @Path("id") int id);
}
