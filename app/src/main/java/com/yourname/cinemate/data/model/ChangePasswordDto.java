package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordDto {
    @SerializedName("oldPassword")
    private final String oldPassword;

    @SerializedName("newPassword")
    private final String newPassword;

    public ChangePasswordDto(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}