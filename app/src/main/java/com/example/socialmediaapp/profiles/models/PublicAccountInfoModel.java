package com.example.socialmediaapp.profiles.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicAccountInfoModel {
    private Long id;
    private String forename;
    private String surname;
    private String country;
    private String city;
}
