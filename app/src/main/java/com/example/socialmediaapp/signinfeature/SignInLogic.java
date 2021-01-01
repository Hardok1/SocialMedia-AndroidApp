package com.example.socialmediaapp.signinfeature;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.socialmediaapp.APIClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInLogic {
    private final SharedPreferences sharedPreferences;
    private final SignInApi signInApi;
    boolean isSuccessfull;

    public SignInLogic(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        signInApi = APIClient.getRetrofit().create(SignInApi.class);
    }

    public boolean signIn(String login, String password, Context context) {
        SignInBody signInBody = new SignInBody(login, password);
        Call<Void> call = signInApi.sendPost(signInBody);
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<Void> response = call.execute();
                        System.out.println("RESPONSE CODE TO: " + response.code());
                        if (response.isSuccessful()) {
                            sharedPreferences.edit().putString("TOKEN", response.headers().get("Authorization")).apply();
                            isSuccessfull = true;
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
        if (isSuccessfull){
            Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        return isSuccessfull;
    }
}
