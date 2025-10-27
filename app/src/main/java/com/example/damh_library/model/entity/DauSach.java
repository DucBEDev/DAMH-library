package com.example.damh_library.model.entity;

import java.util.Date;

public class DauSach {
    private String isbn;
    private String tenSach;
    private String khoSach;
    private String noiDung;
    private int soTrang;
    private String gia;
    private String hinhAnhPath;
    private Date ngayXuatBan;
    private String lanXuatBan;
    private String nhaXB;
    private String maTL;
    private String maNV;

    public DauSach() {
    }

    public DauSach(String isbn, String tenSach, String khoSach, String noiDung, int soTrang, String gia, String hinhAnhPath, Date ngayXuatBan, String lanXuatBan, String nhaXB, String maTL, String maNV) {
        this.isbn = isbn;
        this.tenSach = tenSach;
        this.khoSach = khoSach;
        this.noiDung = noiDung;
        this.soTrang = soTrang;
        this.gia = gia;
        this.hinhAnhPath = hinhAnhPath;
        this.ngayXuatBan = ngayXuatBan;
        this.lanXuatBan = lanXuatBan;
        this.nhaXB = nhaXB;
        this.maTL = maTL;
        this.maNV = maNV;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public String getKhoSach() {
        return khoSach;
    }

    public void setKhoSach(String khoSach) {
        this.khoSach = khoSach;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public int getSoTrang() {
        return soTrang;
    }

    public void setSoTrang(int soTrang) {
        this.soTrang = soTrang;
    }

    public String getGia() {
        return gia;
    }

    public void setGia(String gia) {
        this.gia = gia;
    }

    public String getHinhAnhPath() {
        return hinhAnhPath;
    }

    public void setHinhAnhPath(String hinhAnhPath) {
        this.hinhAnhPath = hinhAnhPath;
    }

    public Date getNgayXuatBan() {
        return ngayXuatBan;
    }

    public void setNgayXuatBan(Date ngayXuatBan) {
        this.ngayXuatBan = ngayXuatBan;
    }

    public String getLanXuatBan() {
        return lanXuatBan;
    }

    public void setLanXuatBan(String lanXuatBan) {
        this.lanXuatBan = lanXuatBan;
    }

    public String getNhaXB() {
        return nhaXB;
    }

    public void setNhaXB(String nhaXB) {
        this.nhaXB = nhaXB;
    }

    public String getMaTL() {
        return maTL;
    }

    public void setMaTL(String maTL) {
        this.maTL = maTL;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }
}
