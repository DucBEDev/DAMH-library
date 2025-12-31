package com.example.damh_library.model.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PhieuMuonPaymentRequest {
    
    @SerializedName("maDG")
    private int maDG;
    
    @SerializedName("hinhThuc")
    private boolean hinhThuc;
    
    @SerializedName("maNV")
    private int maNV;
    
    @SerializedName("danhSachSach")
    private List<SachItem> danhSachSach;
    
    @SerializedName("amount")
    private int amount;
    
    public PhieuMuonPaymentRequest(int maDG, boolean hinhThuc, int maNV, 
                                   List<SachItem> danhSachSach, int amount) {
        this.maDG = maDG;
        this.hinhThuc = hinhThuc;
        this.maNV = maNV;
        this.danhSachSach = danhSachSach;
        this.amount = amount;
    }
    
    // Getters and Setters
    public int getMaDG() { return maDG; }
    public void setMaDG(int maDG) { this.maDG = maDG; }
    
    public boolean isHinhThuc() { return hinhThuc; }
    public void setHinhThuc(boolean hinhThuc) { this.hinhThuc = hinhThuc; }
    
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    
    public List<SachItem> getDanhSachSach() { return danhSachSach; }
    public void setDanhSachSach(List<SachItem> danhSachSach) { this.danhSachSach = danhSachSach; }
    
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    /**
     * Inner class for book item
     */
    public static class SachItem {
        @SerializedName("maSach")
        private String maSach;
        
        @SerializedName("bookStatus")
        private boolean bookStatus;
        
        public SachItem(String maSach, boolean bookStatus) {
            this.maSach = maSach;
            this.bookStatus = bookStatus;
        }
        
        public String getMaSach() { return maSach; }
        public void setMaSach(String maSach) { this.maSach = maSach; }
        
        public boolean isBookStatus() { return bookStatus; }
        public void setBookStatus(boolean bookStatus) { this.bookStatus = bookStatus; }
    }
}
