package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private PaymentData data;
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public PaymentData getData() { return data; }
    
    public static class PaymentData {
        @SerializedName("checkoutUrl")
        private String checkoutUrl;
        
        @SerializedName("orderCode")
        private long orderCode;
        
        @SerializedName("qrCode")
        private String qrCode;
        
        // Getters
        public String getCheckoutUrl() { return checkoutUrl; }
        public long getOrderCode() { return orderCode; }
        public String getQrCode() { return qrCode; }
    }
}
