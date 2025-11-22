package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class Admin {
    @SerializedName("id")
    private String id;

    @SerializedName("full_name")
    private String fullName;

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
}