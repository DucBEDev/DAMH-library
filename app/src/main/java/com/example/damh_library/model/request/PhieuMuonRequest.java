package com.example.damh_library.model.request;

import com.example.damh_library.model.response.BookCartResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhieuMuonRequest {
    @SerializedName("maPhieu")
    public String maPhieu;

    @SerializedName("maDG")
    public long maDG;

    @SerializedName("hinhThuc")
    public boolean hinhThuc;

    @SerializedName("maNV")
    public long maNV;

    @SerializedName("danhSachSach")
    public List<BookCartResponse> danhSachSach;

    public PhieuMuonRequest(long maDG, boolean hinhThuc, long maNV, List<BookCartResponse> danhSachSach) {
//        this.maPhieu = maPhieu;
        this.maDG = maDG;
        this.hinhThuc = hinhThuc;
        this.maNV = maNV;
        this.danhSachSach = danhSachSach;
    }
}


