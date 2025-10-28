package com.example.damh_library.model.response;

public class ReaderProfileResponse {
    private String hoTenDG;      // Họ tên độc giả
    private String emailDG;      // Email độc giả
    private String soCMND;       // Số CMND/CCCD
    private boolean gioiTinh;    // true = Nam, false = Nữ
    private String ngaySinh;     // Ngày sinh (ISO format)
    private String diaChiDG;     // Địa chỉ độc giả
    private String dienThoai;    // Số điện thoại
    private String avatarUrl;    // URL ảnh đại diện (nếu có)

    // Constructor đầy đủ
    public ReaderProfileResponse(String hoTenDG, String emailDG, String soCMND,
                                 boolean gioiTinh, String ngaySinh, String diaChiDG,
                                 String dienThoai) {
        this.hoTenDG = hoTenDG;
        this.emailDG = emailDG;
        this.soCMND = soCMND;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.diaChiDG = diaChiDG;
        this.dienThoai = dienThoai;
    }

    // Constructor rỗng
    public ReaderProfileResponse() {
    }

    // Getters
    public String getHoTenDG() {
        return hoTenDG;
    }

    public String getEmailDG() {
        return emailDG;
    }

    public String getSoCMND() {
        return soCMND;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public String getDiaChiDG() {
        return diaChiDG;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    // Setters
    public void setHoTenDG(String hoTenDG) {
        this.hoTenDG = hoTenDG;
    }

    public void setEmailDG(String emailDG) {
        this.emailDG = emailDG;
    }

    public void setSoCMND(String soCMND) {
        this.soCMND = soCMND;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setDiaChiDG(String diaChiDG) {
        this.diaChiDG = diaChiDG;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}