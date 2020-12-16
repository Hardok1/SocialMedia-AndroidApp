package com.example.socialmediaapp.signupfeature;

import java.util.Set;

import lombok.Data;

@Data
public class SignUpBody {
    private String login;
    private String password;
    private String forename;
    private String surname;
    private String country;
    private String city;
    private Set<String> interests;
}
