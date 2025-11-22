package com.yourname.cinemate.data.model;
import com.google.gson.annotations.SerializedName;
public class ResetPasswordDto {
    @SerializedName("token")
    private final String token;
    @SerializedName("newPassword")
    private final String newPassword;
    public ResetPasswordDto(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }
}