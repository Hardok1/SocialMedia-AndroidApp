package com.example.socialmediaapp.comments.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialmediaapp.APIClient;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.comments.CommentAPI;
import com.example.socialmediaapp.comments.CommentAdapter;
import com.example.socialmediaapp.comments.models.CommentModel;
import com.example.socialmediaapp.posts.PostAPI;
import com.example.socialmediaapp.posts.PostAdapter;
import com.example.socialmediaapp.posts.models.PostDetailsModel;
import com.example.socialmediaapp.posts.models.PostModel;
import com.example.socialmediaapp.profiles.activities.ProfileActivity;

import java.io.IOException;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    long postId;
    boolean isOwner;
    PostDetailsModel postDetailsModel;
    PostAPI postAPI;
    CommentAPI commentAPI;
    String token;
    CommentAdapter commentAdapter;
    RecyclerView recyclerView;
    int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postAPI = APIClient.getRetrofit().create(PostAPI.class);
        commentAPI = APIClient.getRetrofit().create(CommentAPI.class);
        isOwner = getIntent().getBooleanExtra("isOwner", false);
        postId = getIntent().getIntExtra("postId", 0);
        token = getSharedPreferences("app", MODE_PRIVATE).getString("TOKEN", "fail");
        accountId = getSharedPreferences("app", MODE_PRIVATE).getInt("accountId", 0);
        recyclerView = findViewById(R.id.commentsRecyclerView);
        loadComments();
    }

    private void loadComments() {
        Call<PostDetailsModel> call = postAPI.getPostDetails(token, (int)postId);
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<PostDetailsModel> response = call.execute();
                        if (response.isSuccessful()) {
                            if (null != response.body()) {
                                postDetailsModel = response.body();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        commentAdapter = new CommentAdapter(CommentActivity.this, postDetailsModel, token, accountId, commentAPI);
                                        recyclerView.setAdapter(commentAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
                                    }
                                });
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onReloadBtnClick(View v){
        loadComments();
    }

    public void onAddCommentBtnClick(View v){
        EditText commentContentInput = new EditText(this);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Comment")
                .setMessage("Type your comment content")
                .setView(commentContentInput)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addComment(commentContentInput.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();
    }

    private void addComment(String content){
        CommentModel commentModel = new CommentModel();
        commentModel.setContent(content);
        Call<Void> call = commentAPI.addComment(token, (int) postId, commentModel);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(CommentActivity.this, "Comment got successfully added, press reload if you want to see it", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CommentActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
}