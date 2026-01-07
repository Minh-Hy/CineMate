package com.yourname.cinemate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MarkReadDto {
    @SerializedName("ids") private List<String> ids;
    public MarkReadDto(List<String> ids) { this.ids = ids; }
}