package com.yourname.cinemate.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    @SerializedName("id")
    private String id;

    @SerializedName("content")
    private String content;

    @SerializedName("senderType")
    private String senderType; // "USER" hoặc "ADMIN"

    // JSON trả về riêng field user và admin
    @SerializedName("user")
    private User user;

    @SerializedName("admin")
    private Admin admin;

    @SerializedName("createdAt")
    private String createdAt;

    // Trường attachments dạng chuỗi JSON như đã bàn
    @SerializedName("attachments")
    private String attachmentsJson;

    // --- Getters cơ bản ---
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getSenderType() { return senderType; }
    public String getCreatedAt() { return createdAt; }
    public User getUser() { return user; }
    public Admin getAdmin() { return admin; }

    // --- Hàm tiện ích để lấy danh sách file đính kèm ---
    public List<Attachment> getAttachments() {
        if (attachmentsJson == null || attachmentsJson.isEmpty() || attachmentsJson.equals("null")) {
            return new ArrayList<>();
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Attachment>>(){}.getType();
            return gson.fromJson(attachmentsJson, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // --- Hàm tiện ích để lấy Tên người gửi (dù là User hay Admin) ---
    public String getSenderName() {
        if ("ADMIN".equalsIgnoreCase(senderType)) {
            return (admin != null) ? admin.getFullName() : "Admin";
        } else {
            return (user != null) ? user.getDisplayName() : "User";
        }
    }

    // --- Hàm tiện ích để lấy Avatar URL (dù là User hay Admin) ---
    public String getSenderAvatarUrl() {
        if ("ADMIN".equalsIgnoreCase(senderType)) {
            // Admin thường không có avatar url trong DB, trả về null để Adapter hiện ảnh mặc định
            return null;
        } else {
            return (user != null) ? user.getAvatarUrl() : null;
        }
    }
}