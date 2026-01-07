package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class ShareLinks {
    @SerializedName("youtubeUrl")
    private String youtubeUrl;

    @SerializedName("youtubeShortUrl")
    private String youtubeShortUrl;

    @SerializedName("embedUrl")
    private String embedUrl;

    @SerializedName("movieUrl")
    private String movieUrl;

    @SerializedName("tweetText")
    private String tweetText;

    // --- Getters ---
    public String getYoutubeUrl() { return youtubeUrl; }
    public String getYoutubeShortUrl() { return youtubeShortUrl; }
    public String getEmbedUrl() { return embedUrl; }
    public String getMovieUrl() { return movieUrl; }
    public String getTweetText() { return tweetText; }
}