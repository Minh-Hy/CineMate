package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("id")
    private String id;

    @SerializedName("content")
    private String content;

    @SerializedName("senderType")
    private String senderType; // "USER" hoặc "ADMIN"

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("admin")
    private Admin admin; // Thông tin admin nếu tin nhắn từ admin

    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getSenderType() { return senderType; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }
    public Admin getAdmin() { return admin; }

    // Setter cho senderType (dùng khi tạo tin nhắn tạm)
    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }
}