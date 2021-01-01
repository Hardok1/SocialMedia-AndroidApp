package com.example.socialmediaapp.signinfeature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInBody {
    private String login;
    private String password;
}
