package com.example.damh_library.model.response;

import java.util.List;

public class CheckoutHistoryResponse {
    private String maPhieu;
    private boolean hinhThuc;
    private String ngayMuon;
    private int soLuongSach;
    private List<BookInCheckoutResponse> danhSachSach;

    public CheckoutHistoryResponse() {}

    public CheckoutHistoryResponse(String maPhieu, boolean hinhThuc, String ngayMuon, int soLuongSach, List<BookInCheckoutResponse> danhSachSach) {
        this.maPhieu = maPhieu;
        this.hinhThuc = hinhThuc;
        this.ngayMuon = ngayMuon;
        this.soLuongSach = soLuongSach;
        this.danhSachSach = danhSachSach;
    }

    // Getters and setters
    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public boolean isHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(boolean hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public String getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(String ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public int getSoLuongSach() {
        return soLuongSach;
    }

    public void setSoLuongSach(int soLuongSach) {
        this.soLuongSach = soLuongSach;
    }

    public List<BookInCheckoutResponse> getDanhSachSach() {
        return danhSachSach;
    }

    public void setDanhSachSach(List<BookInCheckoutResponse> danhSachSach) {
        this.danhSachSach = danhSachSach;
    }

    @Override
    public String toString() {
        return "CheckoutHistoryResponse{" +
                "maPhieu='" + maPhieu + '\'' +
                ", hinhThuc=" + hinhThuc +
                ", ngayMuon='" + ngayMuon + '\'' +
                ", soLuongSach=" + soLuongSach +
                ", danhSachSach=" + (danhSachSach != null ? danhSachSach.size() : 0) + " books" +
                '}';
    }
}
