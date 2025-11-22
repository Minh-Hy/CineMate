package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
public class RegisterResponse {
    @SerializedName("message") private String message;
    @SerializedName("user") private User user;
    // Getters
    public String getMessage() { return message; }
    public User getUser() { return user; }
}