package com.example.socialmediaapp.comments.models;

import com.example.socialmediaapp.profiles.models.PublicAccountInfoModel;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentModel {
    private long id;
    private String content;
    private String createdAt;
    private PublicAccountInfoModel author;
}
