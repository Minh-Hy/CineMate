package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class PaginatedMovies {
    @SerializedName("items") private List<Movie> data;
    @SerializedName("pagination") private PaginationInfo pagination;
    // Getters
    public List<Movie> getData() {
        return data;
    }
    public PaginationInfo getPagination() {
        return pagination;
    }
}