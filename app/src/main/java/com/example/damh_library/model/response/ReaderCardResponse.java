package com.example.damh_library.model.response;

public class ReaderCardResponse {
    private String maDG; // user id as string
    private String ngayLamThe; // ISO date
    private String ngayHetHan; // ISO date

    public ReaderCardResponse() { }

    public String getMaDG() { return maDG; }
    public String getNgayLamThe() { return ngayLamThe; }
    public String getNgayHetHan() { return ngayHetHan; }

    public void setMaDG(String maDG) { this.maDG = maDG; }
    public void setNgayLamThe(String ngayLamThe) { this.ngayLamThe = ngayLamThe; }
    public void setNgayHetHan(String ngayHetHan) { this.ngayHetHan = ngayHetHan; }
}
