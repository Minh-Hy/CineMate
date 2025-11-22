package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Comment {

    @SerializedName("id")
    private String id; // UUID là String

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    // Quan hệ với User (lồng vào)
    @SerializedName("user")
    private User user;

    // Quan hệ với chính nó (trả lời bình luận)
    @SerializedName("parentCommentId")
    private String parentCommentId;

    // API có thể trả về cả danh sách các câu trả lời
    @SerializedName("replies")
    private List<Comment> replies;

    // --- Getters ---
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public User getUser() { return user; }
    public String getParentCommentId() { return parentCommentId; }
    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }
}