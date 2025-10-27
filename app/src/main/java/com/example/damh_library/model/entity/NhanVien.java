package com.example.damh_library.model.entity;

public class NhanVien {
    private String maNV;
    private String hoTenNV;
    private String diaChi;
    private String dienThoai;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTenNV, String diaChi, String dienThoai) {
        this.maNV = maNV;
        this.hoTenNV = hoTenNV;
        this.diaChi = diaChi;
        this.dienThoai = dienThoai;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTenNV() {
        return hoTenNV;
    }

    public void setHoTenNV(String hoTenNV) {
        this.hoTenNV = hoTenNV;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }
}
