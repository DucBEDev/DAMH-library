package com.example.damh_library.model.response;

public class ReaderCardResponse {
    private String maDG; // user id as string
    private String ngayLamThe; // ISO date
    private String ngayHetHan; // ISO date
    private String avatar;

    public ReaderCardResponse() { }

    public ReaderCardResponse(String maDG, String ngayLamThe, String ngayHetHan, String avatar) {
        this.maDG = maDG;
        this.ngayLamThe = ngayLamThe;
        this.ngayHetHan = ngayHetHan;
        this.avatar = avatar;
    }

    public String getMaDG() { return maDG; }
    public String getNgayLamThe() { return ngayLamThe; }
    public String getNgayHetHan() { return ngayHetHan; }
    public String getAvatar() { return avatar; }

    public void setMaDG(String maDG) { this.maDG = maDG; }
    public void setNgayLamThe(String ngayLamThe) { this.ngayLamThe = ngayLamThe; }
    public void setNgayHetHan(String ngayHetHan) { this.ngayHetHan = ngayHetHan; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
