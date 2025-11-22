package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaginatedComments {

    @SerializedName("comments")
    private List<Comment> comments;

    @SerializedName("pagination")
    private PaginationInfo pagination;

    // Getters
    public List<Comment> getComments() {
        return comments;
    }



    public PaginationInfo getPagination() {
        return pagination;
    }
}