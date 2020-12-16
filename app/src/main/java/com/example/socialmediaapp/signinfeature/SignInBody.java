package com.example.socialmediaapp.signinfeature;

public class SignInBody {
    private String login;
    private String password;

    @Override
    public String toString() {
        return "SignInBody{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public SignInBody(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
