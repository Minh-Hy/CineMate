package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConversationResponse {
    @SerializedName("id")
    private String id; // Đây là conversationId quan trọng

    @SerializedName("status")
    private String status;

    @SerializedName("messages")
    private List<ChatMessage> messages;

    public String getId() { return id; }
    public List<ChatMessage> getMessages() { return messages; }
}