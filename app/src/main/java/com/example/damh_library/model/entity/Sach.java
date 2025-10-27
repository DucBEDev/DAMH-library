package com.example.damh_library.model.entity;

public class Sach {
    private String maSach;
    private String isbn;
    private String tinhTrang;
    private String chuMon;
    private String maNganTu;

    public Sach() {
    }

    public Sach(String maSach, String isbn, String tinhTrang, String chuMon, String maNganTu) {
        this.maSach = maSach;
        this.isbn = isbn;
        this.tinhTrang = tinhTrang;
        this.chuMon = chuMon;
        this.maNganTu = maNganTu;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getChuMon() {
        return chuMon;
    }

    public void setChuMon(String chuMon) {
        this.chuMon = chuMon;
    }

    public String getMaNganTu() {
        return maNganTu;
    }

    public void setMaNganTu(String maNganTu) {
        this.maNganTu = maNganTu;
    }
}
