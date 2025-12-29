package com.example.damh_library.model.request;

import com.google.gson.annotations.SerializedName;

public class PdfChatRequest {
    @SerializedName("filename")
    private String filename;

    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private String userId;

    // Constructor
    public PdfChatRequest(String filename, String message, String userId) {
        this.filename = filename;
        this.message = message;
        this.userId = userId;
    }

    // Getters
    public String getFilename() {
        return filename;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    // Setters
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
