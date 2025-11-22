package com.yourname.cinemate.data.model;
import com.google.gson.annotations.SerializedName;
public class GoogleTokenDto {
    @SerializedName("idToken")
    private final String idToken;
    public GoogleTokenDto(String idToken) { this.idToken = idToken; }
}