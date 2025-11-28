package com.example.damh_library.model.response;

public class BookInCheckoutResponse {
    private String maSach;
    private String tenSach;
    private String hinhAnh;
    private String tacGia;
    private boolean tinhTrangTra;

    public BookInCheckoutResponse() {}

    public BookInCheckoutResponse(String maSach, String tenSach, String hinhAnh, String tacGia, boolean tinhTrangTra) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.hinhAnh = hinhAnh;
        this.tacGia = tacGia;
        this.tinhTrangTra = tinhTrangTra;
    }

    // Getters and setters
    public String getMaSach() {
        return maSach != null ? maSach.trim() : null;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public boolean isTinhTrangTra() {
        return tinhTrangTra;
    }

    public void setTinhTrangTra(boolean tinhTrangTra) {
        this.tinhTrangTra = tinhTrangTra;
    }

    @Override
    public String toString() {
        return "BookInCheckoutResponse{" +
                "maSach='" + maSach + '\'' +
                ", tenSach='" + tenSach + '\'' +
                ", tacGia='" + tacGia + '\'' +
                ", tinhTrangTra=" + tinhTrangTra +
                '}';
    }
}
