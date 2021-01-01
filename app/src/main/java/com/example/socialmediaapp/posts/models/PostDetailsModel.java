package com.example.socialmediaapp.posts.models;

import com.example.socialmediaapp.comments.models.CommentModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailsModel {
    private long id;
    private String content;
    private String createdAt;
    private ArrayList<CommentModel> comments;
}
