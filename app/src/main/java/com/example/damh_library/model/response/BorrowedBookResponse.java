package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

public class BorrowedBookResponse {
    @SerializedName("ISBN")
    private String isbn;

    @SerializedName("borrowDate")
    private String borrowDate; // ISO

    @SerializedName("dueDate")
    private String dueDate; // ISO

    public BorrowedBookResponse() {}

    public String getIsbn() { return isbn; }
    public String getBorrowDate() { return borrowDate; }
    public String getDueDate() { return dueDate; }

    public void setIsbn(String isbn) { this.isbn = isbn != null ? isbn.trim() : null; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public boolean isOverdueDays(int maxDays) {
        if (borrowDate == null || borrowDate.isEmpty()) return false;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date bd = sdf.parse(borrowDate);
            if (bd == null) return false;
            long diff = new java.util.Date().getTime() - bd.getTime();
            long daysDiff = diff / (24L * 60L * 60L * 1000L);
            return daysDiff > maxDays;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isPastDueDate() {
        if (dueDate == null || dueDate.isEmpty()) return false;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date dd = sdf.parse(dueDate);
            if (dd == null) return false;
            return new java.util.Date().after(dd);
        } catch (Exception ex) {
            return false;
        }
    }
}

