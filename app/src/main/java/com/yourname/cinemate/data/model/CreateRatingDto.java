package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
public class CreateRatingDto {
    @SerializedName("score")
    private final int rating;
    public CreateRatingDto(int rating) {
        this.rating = rating;
    }
}