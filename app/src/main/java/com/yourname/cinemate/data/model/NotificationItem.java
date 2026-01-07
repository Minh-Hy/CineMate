package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class NotificationItem {
    @SerializedName("id") private String id;
    @SerializedName("type") private String type; // "COMMENT_REPLY", "MENTION"
    @SerializedName("title") private String title;
    @SerializedName("body") private String body;
    @SerializedName("is_read") private boolean isRead;
    @SerializedName("createdAt") private String createdAt;
    @SerializedName("data") private NotificationData data;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public NotificationData getData() {
        return data;
    }
}