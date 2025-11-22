package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class Rating {
    // Lưu ý: API có thể chỉ trả về một vài trường trong số này
    // tùy vào cách backend thiết kế DTO (Data Transfer Object)

    @SerializedName("score")
    private int score;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Các trường quan hệ
    @SerializedName("userId")
    private String userId;

    @SerializedName("movieId")
    private int movieId;

    // Backend có thể include cả đối tượng User
    @SerializedName("user")
    private User user;

    // --- Getters ---
    public int getScore() { return score; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getUserId() { return userId; }
    public int getMovieId() { return movieId; }
    public User getUser() { return user; }
}