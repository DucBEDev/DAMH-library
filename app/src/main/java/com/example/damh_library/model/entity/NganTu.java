package com.example.damh_library.model.entity;

public class NganTu {
    private String maNganTu;
    private String moTa;
    private String keSach;

    public NganTu() {
    }

    public NganTu(String maNganTu, String moTa, String keSach) {
        this.maNganTu = maNganTu;
        this.moTa = moTa;
        this.keSach = keSach;
    }

    public String getMaNganTu() {
        return maNganTu;
    }

    public void setMaNganTu(String maNganTu) {
        this.maNganTu = maNganTu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getKeSach() {
        return keSach;
    }

    public void setKeSach(String keSach) {
        this.keSach = keSach;
    }
}
