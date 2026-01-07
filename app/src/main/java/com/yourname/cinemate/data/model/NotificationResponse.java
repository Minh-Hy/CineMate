package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationResponse {
    @SerializedName("items") private List<NotificationItem> items;
    @SerializedName("total") private int total;

    public List<NotificationItem> getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }
}