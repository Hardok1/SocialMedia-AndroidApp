package com.example.socialmediaapp.signinfeature;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.socialmediaapp.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInLogic {
    private final SharedPreferences sharedPreferences;
    private final SignInApi signInApi;

    public SignInLogic(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        signInApi = APIClient.getRetrofit().create(SignInApi.class);
    }

    public void signIn(String login, String password, Context context){
        SignInBody signInBody = new SignInBody(login, password);
        Call<Void> call = signInApi.sendPost(signInBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    sharedPreferences.edit().putString("TOKEN", response.headers().get("Authorization")).apply();
                    Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show();
//                Toast.makeText(WelcomeActivity.this, sharedPreferences.getString("TOKEN","fail"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Something went wrong. Check your credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
