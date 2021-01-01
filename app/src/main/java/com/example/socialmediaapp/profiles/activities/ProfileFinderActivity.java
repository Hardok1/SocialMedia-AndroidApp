package com.example.socialmediaapp.profiles.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.APIClient;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.profiles.AccountAPI;
import com.example.socialmediaapp.profiles.ProfileAdapter;
import com.example.socialmediaapp.profiles.models.PublicAccountInfoModel;
import com.example.socialmediaapp.relationships.activities.RelationshipActivity;
import com.example.socialmediaapp.signupfeature.InterestModel;
import com.example.socialmediaapp.signupfeature.SignUpApi;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFinderActivity extends AppCompatActivity {

    private final int FIND_BY_NAME = 0;
    private final int FIND_BY_CITY = 1;
    private final int FIND_BY_COUNTRY = 2;
    private final int FIND_BY_INTEREST = 3;
    private final int FIND_BY_CITY_AND_INTEREST = 4;
    private final int FIND_BY_COUNTRY_AND_INTEREST = 5;

    TextView searchMethodText;
    Button nextProfileButton;
    Button previousProfileButton;
    RecyclerView recyclerView;

    EditText forenameField;
    EditText surnameField;
    EditText countryField;
    EditText cityField;

    private int searchMethod = 0;
    private int page = 0;

    AccountAPI accountAPI;
    List<PublicAccountInfoModel> profiles;
    SignUpApi signUpApi;
    ArrayList<String> interests;
    String chosenInterest="";
    ProfileAdapter profileAdapter;
    String[] interestsArray;
    Toolbar toolbar;

    String token;
    int accountId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_finder);
        accountAPI = APIClient.getRetrofit().create(AccountAPI.class);
        signUpApi = APIClient.getRetrofit().create(SignUpApi.class);
        interests = new ArrayList<>();
        searchMethodText = (TextView) findViewById(R.id.searchMethodTextView);
        searchMethodText.setText("Name");
        nextProfileButton = (Button) findViewById(R.id.nextProfileButton);
        nextProfileButton.setEnabled(false);
        previousProfileButton = (Button) findViewById(R.id.previousProfileButton);
        previousProfileButton.setEnabled(false);
        recyclerView = findViewById(R.id.profilesRecyclerView);
        forenameField = (EditText) findViewById(R.id.forenameSearchingField);
        surnameField = (EditText) findViewById(R.id.surnameSearchingField);
        countryField = (EditText) findViewById(R.id.countrySearchingField);
        cityField = (EditText) findViewById(R.id.citySearchingField);
        token = getSharedPreferences("app", MODE_PRIVATE).getString("TOKEN", "fail");
        accountId = getSharedPreferences("app", MODE_PRIVATE).getInt("accountId", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_nav);
        toolbar.setTitle("Search for profiles");
        setSupportActionBar(toolbar);
        loadInterests();
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
        } else if (itemId == R.id.menu_relationships){
            intent = new Intent(this, RelationshipActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadInterests() {
        Call<List<InterestModel>> call = signUpApi.getInterests();
        call.enqueue(new Callback<List<InterestModel>>() {
            @Override
            public void onResponse(Call<List<InterestModel>> call, Response<List<InterestModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (InterestModel interestModel : response.body()) {
                            interests.add(interestModel.getName());
                        }
                        interestsArray = new String[interests.size()];
                        interests.toArray(interestsArray);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<InterestModel>> call, Throwable t) {
                Toast.makeText(ProfileFinderActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onChangeSearchingProfileMethodBtnClick(View v) {
        String[] methods = {"Name", "City", "Country", "Interest", "City and Interest", "Country and Interest"};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Which method do you want to find profiles by?")
                .setSingleChoiceItems(methods, 0, null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        searchMethod = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        System.out.println("=========");
                        System.out.println("WYBRANA METODA TO: " + searchMethod);
                        searchMethodText.setText(methods[searchMethod]);
                    }
                }).create();
        dialog.show();
        page = 0;
    }

    public void onChooseInterestBtnClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(interestsArray, 0, null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        chosenInterest = interestsArray[selectedPosition];
                    }
                }).create();
        dialog.show();
    }

    public void onFindProfilesBtnClick(View v) {
        switch (searchMethod) {
            case FIND_BY_NAME:
                findByName();
                break;
            case FIND_BY_CITY:
                findByCity();
                break;
            case FIND_BY_COUNTRY:
                findByCountry();
                break;
            case FIND_BY_INTEREST:
                findByInterest();
                break;
            case FIND_BY_CITY_AND_INTEREST:
                findByCityAndInterest();
                break;
            case FIND_BY_COUNTRY_AND_INTEREST:
                findByCountryAndInterest();
                break;
        }
    }

    private void findProfiles(Call<List<PublicAccountInfoModel>> call) {
        call.enqueue(new Callback<List<PublicAccountInfoModel>>() {
            @Override
            public void onResponse(Call<List<PublicAccountInfoModel>> call, Response<List<PublicAccountInfoModel>> response) {
                if (response.isSuccessful()){
                    profiles = response.body();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profileAdapter = new ProfileAdapter(ProfileFinderActivity.this, profiles, accountId);
                            recyclerView.setAdapter(profileAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ProfileFinderActivity.this));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<PublicAccountInfoModel>> call, Throwable t) {

            }
        });
    }

    private void findByName() {
        String forename = forenameField.getText().toString();
        String surname = surnameField.getText().toString();
        if (forename.trim().isEmpty() && surname.trim().isEmpty()){
            Toast.makeText(this, "You need to fill forename and surname field first!", Toast.LENGTH_SHORT).show();
        } else {
            findProfiles(accountAPI.findByName(token, page, forename, surname));
        }
    }

    private void findByCity() {
        String city = cityField.getText().toString();
        if (city.trim().isEmpty()){
            Toast.makeText(this, "You need to fill city field first!", Toast.LENGTH_SHORT).show();
        } else {
            findProfiles(accountAPI.findByCity(token, page, city));
        }
    }

    private void findByCountry() {
        String country = countryField.getText().toString();
        if (country.trim().isEmpty()){
            Toast.makeText(this, "You need to fill country field first!", Toast.LENGTH_SHORT).show();
        } else {
            findProfiles(accountAPI.findByCountry(token, page, country));
        }
    }

    private void findByInterest() {
        if (chosenInterest.trim().isEmpty()){
            Toast.makeText(this, "You need to choose interest first!", Toast.LENGTH_SHORT).show();
        } else {
            findProfiles(accountAPI.findByInterest(token, page, chosenInterest));
        }
    }

    private void findByCityAndInterest() {
        String city = cityField.getText().toString();
        if (city.trim().isEmpty() && chosenInterest.trim().isEmpty()){
            Toast.makeText(this, "You need to fill city field and choose interest first!", Toast.LENGTH_SHORT).show();
        } else {
            findProfiles(accountAPI.findByInterestAndCity(token,page , chosenInterest, city));
        }
    }

    private void findByCountryAndInterest() {
        String country = countryField.getText().toString();
        if (country.trim().isEmpty() && chosenInterest.trim().isEmpty()){
            Toast.makeText(this, "You need to fill country field and choose interest first!", Toast.LENGTH_SHORT).show();
        } else {
            findProfiles(accountAPI.findByInterestAndCountry(token, page, chosenInterest, country));
        }
    }

    //dodaj toolbar
    public void onNextBtnClick(View v){
        page+=1;
        onFindProfilesBtnClick(v);
//        switch (searchMethod) {
//            case FIND_BY_NAME:
//                findByName();
//                break;
//            case FIND_BY_CITY:
//                findByCity();
//                break;
//            case FIND_BY_COUNTRY:
//                findByCountry();
//                break;
//            case FIND_BY_INTEREST:
//                findByInterest();
//                break;
//            case FIND_BY_CITY_AND_INTEREST:
//                findByCityAndInterest();
//                break;
//            case FIND_BY_COUNTRY_AND_INTEREST:
//                findByCountryAndInterest();
//                break;
//        }
    }

    //sprawdz
    public void onPreviousBtnClick(View v){
        if (page > 0){
            page -= 1;
        }
        onFindProfilesBtnClick(v);
    }
}