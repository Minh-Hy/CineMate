package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class DeviceTokenDto {
    @SerializedName("token") private final String token;
    @SerializedName("platform") private final String platform = "android";
    public DeviceTokenDto(String token) { this.token = token; }
}
