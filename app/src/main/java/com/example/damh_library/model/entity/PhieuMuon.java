package com.example.damh_library.model.entity;

import java.util.Date;

public class PhieuMuon {
    private String maPhieu;
    private String maDG;
    private String hinhThuc;
    private Date ngayMuon;
    private String maNV;

    public PhieuMuon() {
    }

    public PhieuMuon(String maPhieu, String maDG, String hinhThuc, Date ngayMuon, String maNV) {
        this.maPhieu = maPhieu;
        this.maDG = maDG;
        this.hinhThuc = hinhThuc;
        this.ngayMuon = ngayMuon;
        this.maNV = maNV;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public String getMaDG() {
        return maDG;
    }

    public void setMaDG(String maDG) {
        this.maDG = maDG;
    }

    public String getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(String hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public Date getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(Date ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }
}
