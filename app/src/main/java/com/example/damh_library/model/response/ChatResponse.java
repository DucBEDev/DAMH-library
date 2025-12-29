package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("answer")
    private String answer;

    @SerializedName("status")
    private String status;

    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    public ChatResponse() {}

    public ChatResponse(String answer, String status) {
        this.answer = answer;
        this.status = status;
    }

    // Getters and Setters
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Helper method to check success
    // Supports both "status": "success" and "success": true formats
    public boolean isSuccess() {
        if (success != null) {
            return success;
        }
        if (status != null) {
            return "success".equalsIgnoreCase(status);
        }
        return false;
    }
}