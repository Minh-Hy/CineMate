package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetail {
    @SerializedName("id") private int id;
    @SerializedName("title") private String title;
    @SerializedName("overview") private String description;
    @SerializedName("genres") private List<Genre> genres;
    @SerializedName("release_date") private String releaseYear;
    @SerializedName("poster_path") private String posterUrl;
    @SerializedName("trailerUrl") private String trailerUrl;
    @SerializedName("director") private String director;
    @SerializedName("vote_average") private double averageRating;
    // Getters

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public String getDirector() {
        return director;
    }

    public double getAverageRating() {
        return averageRating;
    }
}