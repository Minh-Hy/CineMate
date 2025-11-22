package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Lưu ý: Đây là model Movie đơn giản cho danh sách
// API chi tiết có thể trả về một model khác
public class Movie {

    @SerializedName("id")
    private int id; // ID từ TMDB là Int

    @SerializedName("title")
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("trailer")
    private String trailerUrl;
    @SerializedName("is_in_watchlist")
    private boolean isInWatchlist;
    @SerializedName("user_rating")
    private int score;

    // --- Getters ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }
    public double getVoteAverage() { return voteAverage; }
    public String getOverview() { return overview; }
    public String getReleaseDate() { return releaseDate; }
    public List<Genre> getGenres() { return genres; }
    public String getBackdropPath() { return backdropPath; }
    public String getTrailerUrl() { return trailerUrl; }
    public boolean isInWatchlist() {
        return isInWatchlist;
    }
    public int getUserRating() {
        return score;
    }
}