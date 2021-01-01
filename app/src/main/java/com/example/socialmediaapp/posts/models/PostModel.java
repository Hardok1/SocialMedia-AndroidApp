package com.example.socialmediaapp.posts.models;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostModel {
    private long id;
    private String content;
    private String createdAt;
}
