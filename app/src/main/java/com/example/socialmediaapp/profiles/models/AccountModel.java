package com.example.socialmediaapp.profiles.models;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountModel {
    private String password;
    private String forename;
    private String surname;
    private String country;
    private String city;
    private Set<String> interests;

    public AccountModel(AccountDetailsModel accountDetailsModel){
        this.forename = accountDetailsModel.getForename();
        this.surname = accountDetailsModel.getSurname();
        this.country = accountDetailsModel.getCountry();
        this.city = accountDetailsModel.getCity();
        this.interests = accountDetailsModel.getInterests();
    }
}
