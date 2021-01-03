package com.example.socialmediaapp.profiles.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.APIClient;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.chat.ChatAPI;
import com.example.socialmediaapp.chat.ChatActivity;
import com.example.socialmediaapp.posts.PostAPI;
import com.example.socialmediaapp.posts.PostAdapter;
import com.example.socialmediaapp.posts.models.PostModel;
import com.example.socialmediaapp.profiles.AccountAPI;
import com.example.socialmediaapp.profiles.models.AccountDetailsModel;
import com.example.socialmediaapp.relationships.RelationshipAPI;
import com.example.socialmediaapp.relationships.activities.RelationshipActivity;
import com.example.socialmediaapp.relationships.models.RelationshipStatusModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    //PROFILE BELONGS TO LOGGED USER
    private final int USER_PROFILE = 0;
    //PROFILE BELONGS TO FRIEND OF LOGGED USER
    private final int FRIEND_PROFILE = 1;
    //PROFILE BELONGS TO SOMEONE WITHOUT RELATIONSHIP TO USER
    private final int NON_FRIEND_PROFILE = 2;
    //PROFILE BELONGS TO SOMEONE WHO SENT USER FRIEND REQUEST
    private final int NON_FRIEND_REQUESTED_PROFILE = 3;
    //PROFILE BELONGS TO SOMEONE WHO USER SENT FRIEND REQUEST TO
    private final int NON_FRIEND_INVITED_PROFILE = 4;

    private TextView profileNameText;
    private TextView countryNameText;
    private TextView cityNameText;
    private Button showAccountInterestsButton;
    private Button accountSecondActionButton;
    private Button accountActionButton;
    private Button addPostButton;
    private AccountAPI accountAPI;
    private RelationshipAPI relationshipAPI;
    private SharedPreferences sharedPreferences;
    private Long accountId;
    private Set<String> interests;
    private int relationshipStatus;
    private String token;
    private Bundle extras;
    private PostAPI postAPI;
    private ChatAPI chatAPI;
    private int postsPage;
    private List<PostModel> posts;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileNameText = (TextView) findViewById(R.id.profileNameText);
        countryNameText = (TextView) findViewById(R.id.countryNameText);
        cityNameText = (TextView) findViewById(R.id.cityNameText);
        showAccountInterestsButton = (Button) findViewById(R.id.showAccountInterestsButton);
        accountSecondActionButton = (Button) findViewById(R.id.accountSecondActionButton);
        accountActionButton = (Button) findViewById(R.id.accountActionButton);
        addPostButton = (Button) findViewById(R.id.addPostButton);
        recyclerView = findViewById(R.id.postsRecyclerView);
        accountAPI = APIClient.getRetrofit().create(AccountAPI.class);
        relationshipAPI = APIClient.getRetrofit().create(RelationshipAPI.class);
        postAPI = APIClient.getRetrofit().create(PostAPI.class);
        chatAPI = APIClient.getRetrofit().create(ChatAPI.class);
        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        showAccountInterestsButton.setEnabled(false);
        accountSecondActionButton.setEnabled(false);
        accountActionButton.setEnabled(false);
        token = sharedPreferences.getString("TOKEN", "fail");
        extras = getIntent().getExtras();
        postsPage = 0;
        posts = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar_nav);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        loadProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int itemId = item.getItemId();
        if (itemId == R.id.menu_profile) {
            intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.menu_searching) {
            intent = new Intent(this, ProfileFinderActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.menu_relationships){
            intent = new Intent(this, RelationshipActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProfile() {
        fetchRelationshipStatus();
        Call<AccountDetailsModel> call;
        if (relationshipStatus == USER_PROFILE) {
            call = accountAPI.getMyAccountDetails(token);
        } else {
            call = accountAPI.getAccountDetails(token, extras.getInt("id"));
            updateButtons();
        }
        call.enqueue(new Callback<AccountDetailsModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<AccountDetailsModel> call, Response<AccountDetailsModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        profileNameText.setText("Name: " + response.body().getForename() + " " + response.body().getSurname());
                        countryNameText.setText("Country: " + response.body().getCountry());
                        cityNameText.setText("City: " + response.body().getCity());
                        accountId = response.body().getId();
                        if (relationshipStatus == USER_PROFILE) {
                            sharedPreferences.edit().putInt("accountId", accountId.intValue()).apply();
                        }
                        interests = response.body().getInterests();
                        showAccountInterestsButton.setEnabled(true);
                        accountActionButton.setEnabled(true);
                        accountSecondActionButton.setEnabled(true);
                        updateButtons();
                        loadUserPosts();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "We couldn't load this account information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountDetailsModel> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserPosts() {
        Call<List<PostModel>> call = postAPI.getAccountPosts(token, accountId.intValue(), postsPage);
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<List<PostModel>> response = call.execute();
                        if (response.isSuccessful()) {
                            if (null != response.body()) {
                                if (response.body().size() > 0) {
                                    posts.addAll(response.body());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            postAdapter = new PostAdapter(ProfileActivity.this, posts, relationshipStatus == USER_PROFILE, postAPI, token);
                                            recyclerView.setAdapter(postAdapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                                        }
                                    });

                                }
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


    public void onLoadMoreBtnClick(View v) {
        postsPage += 1;
        loadUserPosts();
    }

    private void updateButtons() {
        switch (relationshipStatus) {
            case USER_PROFILE:
                accountActionButton.setText(R.string.edit_account);
                accountSecondActionButton.setVisibility(View.INVISIBLE);
                addPostButton.setVisibility(View.VISIBLE);
                break;
            case FRIEND_PROFILE:
                accountActionButton.setText(R.string.removeFriendship);
                accountSecondActionButton.setVisibility(View.VISIBLE);
                accountSecondActionButton.setText(R.string.send_message);
                break;
            case NON_FRIEND_PROFILE:
                accountActionButton.setText(R.string.send_friend_request);
                accountSecondActionButton.setVisibility(View.INVISIBLE);
                break;
            case NON_FRIEND_REQUESTED_PROFILE:
                accountActionButton.setText(R.string.reject_friendship_request);
                accountSecondActionButton.setVisibility(View.VISIBLE);
                accountSecondActionButton.setText(R.string.accept_request);
                break;
            case NON_FRIEND_INVITED_PROFILE:
                accountActionButton.setText(R.string.cancel_friendship_request);
                accountSecondActionButton.setVisibility(View.INVISIBLE);
                accountSecondActionButton.setText(R.string.cancel_friendship_request);
                break;
        }
    }

    public void onAccountActionBtnClick(View v) {
        accountActionButton.setEnabled(false);
        accountSecondActionButton.setEnabled(false);
        System.out.println("======");
        System.out.println("relacja: " + relationshipStatus);
        if (relationshipStatus == USER_PROFILE) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        } else if (relationshipStatus != NON_FRIEND_PROFILE) {
            System.out.println("NON FRIEND******");
            removeRelationship();
        } else {
            System.out.println("SEND?");
            sendFriendRequest();
        }
        updateButtons();
        accountSecondActionButton.setEnabled(true);
        accountActionButton.setEnabled(true);
    }

    private void sendFriendRequest() {
        Call<Void> call = relationshipAPI.sendFriendRequest(token, accountId.intValue());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    relationshipStatus = NON_FRIEND_INVITED_PROFILE;
                    Toast.makeText(ProfileActivity.this, "Invitation sent", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeRelationship() {
        Call<Void> call = relationshipAPI.removeRelationshipWithUser(token, accountId.intValue());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("JEST RESPONSE");
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Relationship deleted successfully", Toast.LENGTH_SHORT).show();
                    relationshipStatus = NON_FRIEND_PROFILE;
                } else {
                    System.out.println("NIE UDALO SIE!");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onAccountSecondBtnClick(View v) {
        accountActionButton.setEnabled(false);
        accountSecondActionButton.setEnabled(false);
        if (relationshipStatus == FRIEND_PROFILE) {
            System.out.println("====");
            System.out.println("FRIEND PROFILE");
            openChatView();
        } else if (relationshipStatus == NON_FRIEND_REQUESTED_PROFILE) {
            acceptFriendRequest();
        }
        accountSecondActionButton.setEnabled(true);
        accountActionButton.setEnabled(true);
    }

    private void acceptFriendRequest() {
        Call<Void> call = relationshipAPI.acceptFriendRequestWithUser(token, accountId.intValue());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    relationshipStatus = FRIEND_PROFILE;
                    updateButtons();
                    Toast.makeText(ProfileActivity.this, "Request successfully accepted!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChatView() {
        if (null != extras && extras.containsKey("id")) {
            System.out.println("====");
            System.out.println("POSIADA ID");
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("accountId2", extras.getInt("id"));
            Call<Void> call = chatAPI.addChat(token, extras.getInt("id"));
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<Void> response = call.execute();
                        if (response.isSuccessful()){
                            System.out.println("POWIODLO SIE");
//                            int id1 = getSharedPreferences("app", MODE_PRIVATE).getInt("accountId", 0);
//                            int id2 = extras.getInt("id");
//                            String fcmChatId = id1 > id2 ? id2 + "_" + id1 : id1 + "_" + id2;
//                            FirebaseMessaging.getInstance().subscribeToTopic(fcmChatId)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        System.out.println("SUBSCRIBED TO topic: " + fcmChatId);
//                                        startActivity(intent);
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    System.out.println("FAILED subsribe to topic " + fcmChatId + " " + e.getMessage());
//                                }
//                            });

                            startActivity(intent);
                        } else {
                            System.out.println("NIE POWIODLO SIE ZE STATUSEM: " + response.code());
                        }
                    } catch (IOException e) {
                        System.out.println("wyjatek");
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
    }

    public void onShowAccountInterestsBtnClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.select_dialog_item);
        arrayAdapter.addAll(interests);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void fetchRelationshipStatus() {
        if (extras == null) {
            relationshipStatus = USER_PROFILE;
        } else {
            Call<RelationshipStatusModel> call = relationshipAPI.getRelationshipStatus(token, extras.getInt("id"));
            try {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response<RelationshipStatusModel> response = call.execute();
                            if (response.isSuccessful()) {
                                switch (response.body().getStatus()) {
                                    case "friend":
                                        relationshipStatus = FRIEND_PROFILE;
                                        break;
                                    case "invited":
                                        relationshipStatus = NON_FRIEND_INVITED_PROFILE;
                                        break;
                                    case "requested":
                                        relationshipStatus = NON_FRIEND_REQUESTED_PROFILE;
                                        break;
                                    case "stranger":
                                        relationshipStatus = NON_FRIEND_PROFILE;
                                        break;
                                    default:
                                        relationshipStatus = USER_PROFILE;
                                        break;
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
    }

    public void onAddPostBtnClick(View v) {
        EditText postContentInput = new EditText(this);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Post")
                .setMessage("Type your post content")
                .setView(postContentInput)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addPost(postContentInput.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();
    }

    private void addPost(String content) {
        PostModel post = new PostModel();
        post.setContent(content);
        Call<Void> call = postAPI.addPost(token, post);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Post got successfully added, press reload if you want to see it", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onReloadBtnClick(View v) {
        postsPage = 0;
        posts.clear();
        loadProfile();
    }
}