package com.yourname.cinemate.data.model;
import com.google.gson.annotations.SerializedName;
public class ForgotPasswordDto {
    @SerializedName("email")
    private final String email;
    public ForgotPasswordDto(String email) { this.email = email; }
}