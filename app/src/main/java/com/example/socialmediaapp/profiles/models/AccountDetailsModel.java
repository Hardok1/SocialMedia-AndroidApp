package com.example.socialmediaapp.profiles.models;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsModel {
    private Long id;
    private String forename;
    private String surname;
    private String country;
    private String city;
    private Set<String> interests;

    public AccountDetailsModel(AccountModel accountModel){
        this.forename = accountModel.getForename();
        this.surname = accountModel.getSurname();
        this.country = accountModel.getCountry();
        this.city = accountModel.getCity();
        this.interests = accountModel.getInterests();
    }
}
