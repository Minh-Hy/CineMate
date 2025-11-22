package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
}