package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

public class VerifyPaymentResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("paymentStatus")
    private String paymentStatus; // PAID, PENDING, CANCELLED, EXPIRED
    
    @SerializedName("phieuMuonCreated")
    private boolean phieuMuonCreated;
    
    @SerializedName("data")
    private VerifyData data;
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getPaymentStatus() { return paymentStatus; }
    public boolean isPhieuMuonCreated() { return phieuMuonCreated; }
    public VerifyData getData() { return data; }
    
    public static class VerifyData {
        @SerializedName("maDG")
        private int maDG;
        
        @SerializedName("orderCode")
        private long orderCode;
        
        public int getMaDG() { return maDG; }
        public long getOrderCode() { return orderCode; }
    }
}
