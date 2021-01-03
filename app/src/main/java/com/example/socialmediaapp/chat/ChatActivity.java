package com.example.socialmediaapp.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.socialmediaapp.APIClient;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.chat.model.MessageModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    EditText messageField;
    RecyclerView recyclerView;
    ChatAPI chatAPI;
    String token;
    int accountId;
    int accountId2;
    MessagesAdapter messagesAdapter;
    List<MessageModel> messageModels;
    BroadcastReceiver updateUIReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageField = (EditText) findViewById(R.id.messageField);
        recyclerView = findViewById(R.id.messagesRecyclerView);
        chatAPI = APIClient.getRetrofit().create(ChatAPI.class);
        token = getSharedPreferences("app", MODE_PRIVATE).getString("TOKEN", "fail");
        accountId = getSharedPreferences("app", MODE_PRIVATE).getInt("accountId", 0);
        accountId2 = getIntent().getExtras().getInt("accountId2");
        String fcmChatId = accountId > accountId2 ? accountId2 + "_" + accountId : accountId + "_" + accountId2;
        FirebaseMessaging.getInstance().subscribeToTopic(fcmChatId)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("SUBSCRIBED TO topic: " + fcmChatId);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("FAILED TO SUBSCRIBE to topic: " + fcmChatId + " " + e.getMessage());
                }
            });

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.socialmediaapp.fcmAction");
        updateUIReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadMessages();
            }
        };
        registerReceiver(updateUIReceiver, filter);

        loadMessages();
        checkGooglePlayServices();
    }

    private void checkGooglePlayServices() {
        int googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (googlePlayServicesAvailable != ConnectionResult.SUCCESS) {
            System.out.println("Google play Error");
        } else {
            System.out.println("Google play services updated");
        }
    }

    private void loadMessages() {
        Call<List<MessageModel>> call = chatAPI.getMessages(token, accountId2);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<List<MessageModel>> response = call.execute();
                    if (response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageModels = response.body();
                                messagesAdapter = new MessagesAdapter(messageModels, ChatActivity.this);
                                recyclerView.setAdapter(messagesAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onMessageSendBtnClick(View v){
        MessageModel messageModel = new MessageModel();
        messageModel.setContent(messageField.getText().toString());
        Call<Void> call = chatAPI.addMessage(token, messageModel, accountId2);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                     messageField.setText("");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        String topicName = accountId > accountId2 ? accountId2 + "_" + accountId : accountId + "_" + accountId2;
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName);
        unregisterReceiver(updateUIReceiver);
        super.onDestroy();
    }
}