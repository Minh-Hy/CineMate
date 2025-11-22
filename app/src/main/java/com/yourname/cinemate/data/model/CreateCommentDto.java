package com.yourname.cinemate.data.model;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class CreateCommentDto {
    @SerializedName("content")
    private final String content;

    @SerializedName("parentCommentId")
    @Nullable
    private final String parentCommentId;

    // Constructor cho bình luận gốc
    public CreateCommentDto(String content) {
        this.content = content;
        this.parentCommentId = null;
    }

    // Constructor cho bình luận trả lời
    public CreateCommentDto(String content, @Nullable String parentCommentId) {
        this.content = content;
        this.parentCommentId = parentCommentId;
    }
}