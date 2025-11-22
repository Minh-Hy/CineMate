package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;

    // THÊM TRƯỜG MỚI
    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("user")
    private User user;

    // --- Getters ---
    public String getAccessToken() {
        return accessToken;
    }

    // THÊM GETTER MỚI
    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }
}