package com.example.damh_library.model.entity;

public class TacGiaSach {
    private String maTG;
    private String isbn;

    public TacGiaSach() {
    }

    public TacGiaSach(String maTG, String isbn) {
        this.maTG = maTG;
        this.isbn = isbn;
    }

    public String getMaTG() {
        return maTG;
    }

    public void setMaTG(String maTG) {
        this.maTG = maTG;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
