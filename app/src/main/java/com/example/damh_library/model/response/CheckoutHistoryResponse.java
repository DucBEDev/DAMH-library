package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

public class CheckoutHistoryResponse {
    @SerializedName("maPhieu")
    private Long maPhieu;

    @SerializedName("hinhThuc")
    private Boolean hinhThuc;

    @SerializedName("ngayMuon")
    private String ngayMuon;

    public CheckoutHistoryResponse() {}

    public CheckoutHistoryResponse(long maPhieu, boolean hinhThuc, String ngayMuon) {
        this.maPhieu = maPhieu;
        this.hinhThuc = hinhThuc;
        this.ngayMuon = ngayMuon;
    }

    public long getMaphieu() {
        return maPhieu;
    }

    public void setMaphieu(long maPhieu) {
        this.maPhieu = maPhieu;
    }

    public boolean isHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(boolean hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public String getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(String ngayMuon) {
        this.ngayMuon = ngayMuon;
    }
}
