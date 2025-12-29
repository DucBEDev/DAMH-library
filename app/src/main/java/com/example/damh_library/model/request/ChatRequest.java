package com.example.damh_library.model.request;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("filename")
    private String filename; // Optional - for PDF chat

    // Constructor for general chat (LibraryAssistantFragment)
    public ChatRequest(String message, String userId) {
        this.message = message;
        this.user_id = userId != null ? userId : "";
        this.filename = null;
    }

    // Constructor for PDF chat (PdfReaderFragment)
    public ChatRequest(String message, String userId, String filename) {
        this.message = message;
        this.user_id = userId != null ? userId : "";
        this.filename = filename;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}