package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String message;
    private boolean isSuccess;
    private String userName;
    private String userType;
    @SerializedName("email")
    private String userEmail;

    public LoginResponse(String message, boolean isSuccess, String userName, String userType, String userEmail) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.userName = userName;
        this.userType = userType;
        this.userEmail = userEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
