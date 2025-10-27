package com.example.damh_library.model.entity;

import java.util.Date;

public class DocGia {
    private String maDG;
    private String hoDG;
    private String tenDG;
    private String emailDG;
    private String gioiTinh;
    private Date ngaySinh;
    private String diaChiDG;
    private String dienThoaiDG;
    private String soCMND;
    private Date ngayLamThe;
    private Date ngayHetHan;
    private String hoatDong;

    public DocGia() {
    }

    public DocGia(String maDG, String hoDG, String tenDG, String emailDG, String gioiTinh, Date ngaySinh, String diaChiDG, String dienThoaiDG, String soCMND, Date ngayLamThe, Date ngayHetHan, String hoatDong) {
        this.maDG = maDG;
        this.hoDG = hoDG;
        this.tenDG = tenDG;
        this.emailDG = emailDG;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.diaChiDG = diaChiDG;
        this.dienThoaiDG = dienThoaiDG;
        this.soCMND = soCMND;
        this.ngayLamThe = ngayLamThe;
        this.ngayHetHan = ngayHetHan;
        this.hoatDong = hoatDong;
    }

    public String getMaDG() {
        return maDG;
    }

    public void setMaDG(String maDG) {
        this.maDG = maDG;
    }

    public String getHoDG() {
        return hoDG;
    }

    public void setHoDG(String hoDG) {
        this.hoDG = hoDG;
    }

    public String getTenDG() {
        return tenDG;
    }

    public void setTenDG(String tenDG) {
        this.tenDG = tenDG;
    }

    public String getEmailDG() {
        return emailDG;
    }

    public void setEmailDG(String emailDG) {
        this.emailDG = emailDG;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDiaChiDG() {
        return diaChiDG;
    }

    public void setDiaChiDG(String diaChiDG) {
        this.diaChiDG = diaChiDG;
    }

    public String getDienThoaiDG() {
        return dienThoaiDG;
    }

    public void setDienThoaiDG(String dienThoaiDG) {
        this.dienThoaiDG = dienThoaiDG;
    }

    public String getSoCMND() {
        return soCMND;
    }

    public void setSoCMND(String soCMND) {
        this.soCMND = soCMND;
    }

    public Date getNgayLamThe() {
        return ngayLamThe;
    }

    public void setNgayLamThe(Date ngayLamThe) {
        this.ngayLamThe = ngayLamThe;
    }

    public Date getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(Date ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public String getHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(String hoatDong) {
        this.hoatDong = hoatDong;
    }
}
