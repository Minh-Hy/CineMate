package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

public class Attachment {
    @SerializedName("type")
    private String type; // Ví dụ: "file", "image"

    @SerializedName("url")
    private String url;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("fileSize")
    private long fileSize;

    // --- Getters ---
    public String getType() { return type; }
    public String getUrl() { return url; }
    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
}