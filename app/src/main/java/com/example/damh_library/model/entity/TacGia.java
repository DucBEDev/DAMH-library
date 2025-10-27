package com.example.damh_library.model.entity;

public class TacGia {
    private String maTacGia;
    private String hoTenTG;
    private String diaChiTG;
    private String dienThoaiTG;

    public TacGia() {
    }

    public TacGia(String maTacGia, String hoTenTG, String diaChiTG, String dienThoaiTG) {
        this.maTacGia = maTacGia;
        this.hoTenTG = hoTenTG;
        this.diaChiTG = diaChiTG;
        this.dienThoaiTG = dienThoaiTG;
    }

    public String getMaTacGia() {
        return maTacGia;
    }

    public void setMaTacGia(String maTacGia) {
        this.maTacGia = maTacGia;
    }

    public String getHoTenTG() {
        return hoTenTG;
    }

    public void setHoTenTG(String hoTenTG) {
        this.hoTenTG = hoTenTG;
    }

    public String getDiaChiTG() {
        return diaChiTG;
    }

    public void setDiaChiTG(String diaChiTG) {
        this.diaChiTG = diaChiTG;
    }

    public String getDienThoaiTG() {
        return dienThoaiTG;
    }

    public void setDienThoaiTG(String dienThoaiTG) {
        this.dienThoaiTG = dienThoaiTG;
    }
}
