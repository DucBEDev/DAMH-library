package com.example.damh_library.model.entity;

import java.util.Date;

public class CtPhieuMuon {
    private String maSach;
    private String maPhieu;
    private Date ngayTra;
    private String tinhTrangMuon;
    private String tra;
    private String maNVNS;

    public CtPhieuMuon() {
    }

    public CtPhieuMuon(String maSach, String maPhieu, Date ngayTra, String tinhTrangMuon, String tra, String maNVNS) {
        this.maSach = maSach;
        this.maPhieu = maPhieu;
        this.ngayTra = ngayTra;
        this.tinhTrangMuon = tinhTrangMuon;
        this.tra = tra;
        this.maNVNS = maNVNS;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public Date getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(Date ngayTra) {
        this.ngayTra = ngayTra;
    }

    public String getTinhTrangMuon() {
        return tinhTrangMuon;
    }

    public void setTinhTrangMuon(String tinhTrangMuon) {
        this.tinhTrangMuon = tinhTrangMuon;
    }

    public String getTra() {
        return tra;
    }

    public void setTra(String tra) {
        this.tra = tra;
    }

    public String getMaNVNS() {
        return maNVNS;
    }

    public void setMaNVNS(String maNVNS) {
        this.maNVNS = maNVNS;
    }
}
