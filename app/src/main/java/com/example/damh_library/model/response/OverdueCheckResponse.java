package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// OverdueCheckResponse.java
public class OverdueCheckResponse {
    private List<OverdueBookInfo> overdueBooks;

    public List<OverdueBookInfo> getOverdueBooks() { return overdueBooks; }

    public class OverdueBookInfo {
        private long maPhieu;
        private String tenSach;

        public long getMaPhieu() { return maPhieu; }
        public String getTenSach() { return tenSach; }
    }


}

