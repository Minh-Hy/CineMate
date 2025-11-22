package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConversationResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private String status;

    @SerializedName("messages")
    private List<ChatMessage> messages;

    // Getters
    public String getId() { return id; }
    public String getStatus() { return status; }
    public List<ChatMessage> getMessages() { return messages; }
}