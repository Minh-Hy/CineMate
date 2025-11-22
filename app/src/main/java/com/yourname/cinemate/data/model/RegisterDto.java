package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterDto {
    @SerializedName("display_name")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    public RegisterDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}