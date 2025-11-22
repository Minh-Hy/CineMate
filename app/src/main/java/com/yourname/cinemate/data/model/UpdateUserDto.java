package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateUserDto {
    @SerializedName("display_name")
    private final String displayName;

    public UpdateUserDto(String displayName) {
        this.displayName = displayName;
    }
}