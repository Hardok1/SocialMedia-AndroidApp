package com.example.socialmediaapp.signinfeature;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.profiles.activities.ProfileActivity;
import com.example.socialmediaapp.signupfeature.SignUpActivity;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private EditText loginField;
    private EditText passwordField;
    private Button signInButton;
    private SignInLogic signInLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        loginField = (EditText) findViewById(R.id.loginField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        signInButton = (Button) findViewById(R.id.signInButton);
        signInLogic = new SignInLogic(getSharedPreferences("app", MODE_PRIVATE));
    }

    public void onLoginBtnClick(View v) {
        signInButton.setEnabled(false);
        if (signInLogic.signIn(loginField.getText().toString(), passwordField.getText().toString(), WelcomeActivity.this)) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            signInButton.setEnabled(true);
        }
    }

    public void onRegisterBtnClick(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }


}