package com.example.damh_library.model.response;

public class FavoriteBookResponse {
    private String isbn;
    private String tenSach;
    private String tacGia;
    private String nxb;
    private String ngayThem;
    private String imageUrl;

    public FavoriteBookResponse(String isbn, String tenSach, String tacGia, String nxb, String ngayThem) {
        this.isbn = isbn;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.nxb = nxb;
        this.ngayThem = ngayThem;
    }

    public FavoriteBookResponse(String isbn, String tenSach, String tacGia, String nxb, String ngayThem, String imageUrl) {
        this.isbn = isbn;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.nxb = nxb;
        this.ngayThem = ngayThem;
        this.imageUrl = imageUrl;
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

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getNxb() {
        return nxb;
    }

    public void setNxb(String nxb) {
        this.nxb = nxb;
    }

    public String getNgayThem() {
        return ngayThem;
    }

    public void setNgayThem(String ngayThem) {
        this.ngayThem = ngayThem;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
