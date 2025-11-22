package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private String id; // UUID là String

    @SerializedName("email")
    private String email;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("provider")
    private String provider;

    @SerializedName("created_at")
    private String createdAt;

    // --- Getters ---
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getProvider() { return provider; }
    public String getCreatedAt() { return createdAt; }
}