package com.example.socialmediaapp.profiles.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialmediaapp.APIClient;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.profiles.AccountAPI;
import com.example.socialmediaapp.profiles.models.AccountDetailsModel;
import com.example.socialmediaapp.profiles.models.AccountModel;
import com.example.socialmediaapp.signinfeature.WelcomeActivity;
import com.example.socialmediaapp.signupfeature.InterestModel;
import com.example.socialmediaapp.signupfeature.SignUpApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditProfileActivity extends AppCompatActivity {

    //TODO: wyszukiwarka, czat, nawigacja drawerem
    private final List<String> interests = new ArrayList<>();
    private AccountDetailsModel accountDetailsModel;
    private Set<String> chosenInterests;
    private SignUpApi signUpApi;
    private AccountAPI accountAPI;
    private Button saveButton;
    private EditText passwordField;
    private EditText forenameField;
    private EditText surnameField;
    private EditText countryField;
    private EditText cityField;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        saveButton = (Button) findViewById(R.id.saveEditedFieldsButton);
        saveButton.setEnabled(false);
        chosenInterests = new HashSet<>();
        signUpApi = APIClient.getRetrofit().create(SignUpApi.class);
        accountAPI = APIClient.getRetrofit().create(AccountAPI.class);
        passwordField = (EditText) findViewById(R.id.editedPasswordField);
        forenameField = (EditText) findViewById(R.id.editedForenameField);
        surnameField = (EditText) findViewById(R.id.editedSurnameField);
        countryField = (EditText) findViewById(R.id.editedCountryField);
        cityField = (EditText) findViewById(R.id.editedCityField);
        token = getSharedPreferences("app", MODE_PRIVATE).getString("TOKEN", "fail");
        getInterests();
        loadAccount();
    }

    private void loadAccount() {
        Call<AccountDetailsModel> call = accountAPI.getMyAccountDetails(token);
        call.enqueue(new Callback<AccountDetailsModel>() {
            @Override
            public void onResponse(Call<AccountDetailsModel> call, Response<AccountDetailsModel> response) {
                if (response.isSuccessful()) {
                    accountDetailsModel = response.body();
                    forenameField.setText(accountDetailsModel.getForename());
                    surnameField.setText(accountDetailsModel.getSurname());
                    countryField.setText(accountDetailsModel.getSurname());
                    cityField.setText(accountDetailsModel.getCity());
                    chosenInterests = accountDetailsModel.getInterests();
                    saveButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<AccountDetailsModel> call, Throwable t) {

            }
        });
    }

    private void getInterests() {
        Call<List<InterestModel>> call = signUpApi.getInterests();
        call.enqueue(new Callback<List<InterestModel>>() {
            @Override
            public void onResponse(Call<List<InterestModel>> call, Response<List<InterestModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (InterestModel interestModel : response.body()) {
                            interests.add(interestModel.getName());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<InterestModel>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onChooseInterestsBtnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        String[] interestsArray = new String[interests.size()];
        interests.toArray(interestsArray);
        final boolean[] checkedInterests = new boolean[interests.size()];
        if (chosenInterests.size() > 0) {
            for (int i = 0; i < checkedInterests.length; i++) {
                for (String chIntr : chosenInterests) {
                    if (interestsArray[i].equals(chIntr)) {
                        checkedInterests[i] = true;
                    }
                }
            }
        }
        builder.setMultiChoiceItems(interestsArray, checkedInterests, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                checkedInterests[i] = isChecked;
            }
        });
        builder.setCancelable(false);
        builder.setTitle("Choose what you are interested in");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedInterests.length; i++) {
                    if (checkedInterests[i]) {
                        chosenInterests.add(interestsArray[i]);
                    } else {
                        chosenInterests.remove(interestsArray[i]);
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSaveBtnClick(View view) {
        saveButton.setEnabled(false);
        if (passwordField.getText().toString().isEmpty() || forenameField.getText().toString().isEmpty() ||
                surnameField.getText().toString().isEmpty() || countryField.getText().toString().isEmpty() ||
                cityField.getText().toString().isEmpty() || chosenInterests.size() < 1) {
            Toast.makeText(this, "You need to fill every field and choose your interests first!", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
        } else {
            AccountDetailsModel newAccountDetails = new AccountDetailsModel();
            newAccountDetails.setForename(forenameField.getText().toString());
            newAccountDetails.setSurname(surnameField.getText().toString());
            newAccountDetails.setCountry(countryField.getText().toString());
            newAccountDetails.setCity(cityField.getText().toString());
            newAccountDetails.setInterests(chosenInterests);
            newAccountDetails.setId(accountDetailsModel.getId());
            if (newAccountDetails.equals(accountDetailsModel)){
                Toast.makeText(this, "You haven't changed anything!", Toast.LENGTH_SHORT).show();
                saveButton.setEnabled(true);
            } else {
                AccountModel accountModel = new AccountModel(newAccountDetails);
                accountModel.setPassword(passwordField.getText().toString());
                Call<Void> call = accountAPI.editAccount(token, accountModel);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()){
                            accountDetailsModel = newAccountDetails;
                            afterSuccessfulSave();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
                        saveButton.setEnabled(true);
                    }
                });
            }
        }
    }

    private void afterSuccessfulSave() {
        Toast.makeText(this, "Changes successfully saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onDeleteAccountBtnClick(View v){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Deleting account")
                .setMessage("Are you sure you want to remove your account?")
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<Void> call = accountAPI.deleteAccount(token);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()){
                                    getSharedPreferences("app", MODE_PRIVATE).edit().remove("TOKEN").apply();
                                    getSharedPreferences("app", MODE_PRIVATE).edit().remove("accountId").apply();
                                    Intent intent = new Intent(EditProfileActivity.this, WelcomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                    }
                })
                .create();
        dialog.show();
    }
}