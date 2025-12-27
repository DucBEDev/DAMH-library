package com.example.damh_library.model.request;

import com.google.gson.annotations.SerializedName;

public class UpdatePasswordRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("newPassword")
    private String newPassword;

    public UpdatePasswordRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "UpdatePasswordRequest{" +
                "email='" + email + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
