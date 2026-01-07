package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class NotificationData {
    @SerializedName("deeplink") private String deeplink;
    @SerializedName("movieId") private int movieId;

    public String getDeeplink() {
        return deeplink;
    }

    public int getMovieId() {
        return movieId;
    }
}
