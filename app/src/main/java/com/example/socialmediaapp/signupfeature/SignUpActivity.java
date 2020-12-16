package com.example.socialmediaapp.signupfeature;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialmediaapp.APIClient;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.signinfeature.SignInLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private final List<String> interests = new ArrayList<>();
    private Set<String> choosedInterests;
    private SignUpApi signUpApi;
    private Button signUpButton;
    private EditText loginField;
    private EditText passwordField;
    private EditText forenameField;
    private EditText surnameField;
    private EditText countryField;
    private EditText cityField;
    private SignInLogic signInLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        choosedInterests = new HashSet<>();
        signUpApi = APIClient.getRetrofit().create(SignUpApi.class);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        loginField = (EditText) findViewById(R.id.signUpLoginField);
        passwordField = (EditText) findViewById(R.id.signUpPasswordField);
        forenameField = (EditText) findViewById(R.id.forenameField);
        surnameField = (EditText) findViewById(R.id.surnameField);
        countryField = (EditText) findViewById(R.id.countryField);
        cityField = (EditText) findViewById(R.id.cityField);
        signInLogic = new SignInLogic(getSharedPreferences("app", MODE_PRIVATE));
        getInterests();
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
                Toast.makeText(SignUpActivity.this, "Something went wrong. Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onChooseInterestsBtnClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        String[] interestsArray = new String[interests.size()];
        interests.toArray(interestsArray);
        final boolean[] checkedInterests = new boolean[interests.size()];
        if (choosedInterests.size() > 0) {
            for (int i = 0; i < checkedInterests.length; i++) {
                for (String chIntr : choosedInterests) {
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
                        choosedInterests.add(interestsArray[i]);
                    } else {
                        choosedInterests.remove(interestsArray[i]);
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSignUpBtnClick(View v) {
        signUpButton.setEnabled(false);
        if (loginField.getText().toString().isEmpty() || passwordField.getText().toString().isEmpty() ||
                forenameField.getText().toString().isEmpty() || surnameField.getText().toString().isEmpty() ||
                countryField.getText().toString().isEmpty() || cityField.getText().toString().isEmpty() ||
                choosedInterests.size() < 1) {
            Toast.makeText(this, "You need to fill every field and choose your interests first!", Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true);
        } else {
            SignUpBody signUpBody = new SignUpBody();
            signUpBody.setLogin(loginField.getText().toString());
            signUpBody.setPassword(passwordField.getText().toString());
            signUpBody.setForename(forenameField.getText().toString());
            signUpBody.setSurname(surnameField.getText().toString());
            signUpBody.setCountry(countryField.getText().toString());
            signUpBody.setCity(cityField.getText().toString());
            signUpBody.setInterests(choosedInterests);
            Call<Void> call = signUpApi.sendPost(signUpBody);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Signed up succesfully!", Toast.LENGTH_SHORT).show();
                        signInLogic.signIn(loginField.getText().toString(), passwordField.getText().toString(), SignUpActivity.this);
                    } else if (response.code() == 409) {
                        Toast.makeText(SignUpActivity.this, "This login already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Something went wrong, check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
        signUpButton.setEnabled(true);
    }


}