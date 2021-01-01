package com.example.socialmediaapp.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageModel {
    String content;
    String createdAt;
    String name;
}
