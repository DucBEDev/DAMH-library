package com.example.damh_library.model.request;

import com.google.gson.annotations.SerializedName;

public class UpdateClientProfileRequest {
    @SerializedName("hoTenDG")
    private String hoTenDG;

    @SerializedName("emailDG")
    private String emailDG;

    @SerializedName("soCMND")
    private String soCMND;

    @SerializedName("gioiTinh")
    private String gioiTinh; // send as "1" or "0"

    @SerializedName("ngaySinh")
    private String ngaySinh; // format dd/MM/yyyy

    @SerializedName("diaChiDG")
    private String diaChiDG;

    @SerializedName("dienThoai")
    private String dienThoai;

    public UpdateClientProfileRequest(String hoTenDG, String emailDG, String soCMND, String gioiTinh, String ngaySinh, String diaChiDG, String dienThoai) {
        this.hoTenDG = hoTenDG;
        this.emailDG = emailDG;
        this.soCMND = soCMND;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.diaChiDG = diaChiDG;
        this.dienThoai = dienThoai;
    }

    // getters
    public String getHoTenDG() { return hoTenDG; }
    public String getEmailDG() { return emailDG; }
    public String getSoCMND() { return soCMND; }
    public String getGioiTinh() { return gioiTinh; }
    public String getNgaySinh() { return ngaySinh; }
    public String getDiaChiDG() { return diaChiDG; }
    public String getDienThoai() { return dienThoai; }
}

