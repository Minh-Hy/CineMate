package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenDto {
    @SerializedName("refresh_token")
    private final String refreshToken;
    public RefreshTokenDto(String refreshToken) { this.refreshToken = refreshToken; }
}