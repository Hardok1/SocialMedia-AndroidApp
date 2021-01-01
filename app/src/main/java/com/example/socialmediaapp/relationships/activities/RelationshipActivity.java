package com.example.socialmediaapp.relationships.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialmediaapp.APIClient;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.profiles.ProfileAdapter;
import com.example.socialmediaapp.profiles.activities.ProfileActivity;
import com.example.socialmediaapp.profiles.activities.ProfileFinderActivity;
import com.example.socialmediaapp.profiles.models.PublicAccountInfoModel;
import com.example.socialmediaapp.relationships.RelationshipAPI;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Response;

public class RelationshipActivity extends AppCompatActivity {


    private final int FRIEND_PROFILE = 0;
    private final int NON_FRIEND_REQUESTED_PROFILE = 1;
    private final int NON_FRIEND_INVITED_PROFILE = 2;

    int accountId;
    String token;
    RelationshipAPI relationshipAPI;
    TextView relationshipTypeText;
    RecyclerView recyclerView;
    Toolbar toolbar;
    ProfileAdapter profileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship);
        relationshipAPI = APIClient.getRetrofit().create(RelationshipAPI.class);
        token = getSharedPreferences("app", MODE_PRIVATE).getString("TOKEN", "fail");
        accountId = getSharedPreferences("app", MODE_PRIVATE).getInt("accountId", 0);
        relationshipTypeText = (TextView) findViewById(R.id.relationshipTypeText);
        recyclerView = findViewById(R.id.relationshipRecyclerView);
        toolbar = (Toolbar) findViewById(R.id.relationships_toolbar_nav);
        toolbar.setTitle("Relationships");
        setSupportActionBar(toolbar);
        relationshipTypeText.setText(R.string.friends);
        findRelationships(FRIEND_PROFILE);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void findRelationships(int relationshipType) {
        try {
            final Call<List<PublicAccountInfoModel>> call;
            switch (relationshipType) {
                case NON_FRIEND_REQUESTED_PROFILE:
                    call = relationshipAPI.getReceivedRequests(token);
                    break;
                case NON_FRIEND_INVITED_PROFILE:
                    call = relationshipAPI.getSentRequests(token);
                    break;
                case FRIEND_PROFILE:
                default:
                    call = relationshipAPI.getFriendsList(token);
                    break;
            }
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<List<PublicAccountInfoModel>> response = call.execute();
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profileAdapter = new ProfileAdapter(RelationshipActivity.this, response.body(), accountId);
                                    recyclerView.setAdapter(profileAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(RelationshipActivity.this));
                                }
                            });
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

    public void onChangeRelationshipTypeBtnClick(View v){
        String[] relationshipTypes = {"Friends", "Received invitations", "Sent invitations"};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Which type of relationship do you want to show")
                .setSingleChoiceItems(relationshipTypes, 0, null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        relationshipTypeText.setText(relationshipTypes[selectedPosition]);
                        findRelationships(selectedPosition);
                    }
                }).create();
        dialog.show();
    }
}