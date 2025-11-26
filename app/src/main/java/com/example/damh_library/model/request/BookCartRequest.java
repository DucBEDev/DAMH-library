package com.example.damh_library.model.request;

import com.google.gson.annotations.SerializedName;

public class BookCartRequest {
    @SerializedName("maDG")
    private long maDG;

    @SerializedName("maSach")
    private String maSach;

    public BookCartRequest(long maDG, String maSach) {
        this.maDG = maDG;
        this.maSach = maSach;
    }
}
