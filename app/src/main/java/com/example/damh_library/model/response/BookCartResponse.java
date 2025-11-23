package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class BookCartResponse {

    @SerializedName("ISBN")
    private String isbn;

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("imageUrl")
    private String imageUrl;

    // optional field - some endpoints may provide available count
    @SerializedName("soLuongKhaDung")
    private Integer soLuongKhaDung;

    @SerializedName("addedDate")
    private String addedDate;

    // Transient UI-only field to track selection in cart (not serialized)
    private transient boolean selected = false;

    // Constructors
    public BookCartResponse() {
    }

    public BookCartResponse(String isbn, String title, String author, String publisher,
                            String imageUrl, Integer soLuongKhaDung, String addedDate) {
        this.isbn = normalizeIsbn(isbn);
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.soLuongKhaDung = soLuongKhaDung;
        this.addedDate = addedDate;
    }

    private String normalizeIsbn(String raw) {
        if (raw == null) return null;
        return raw.trim();
    }

    public BookCartResponse(String isbn, String title, String author, String publisher, Integer soLuongKhaDung, String addedDate) {
        this.isbn = normalizeIsbn(isbn);
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.soLuongKhaDung = soLuongKhaDung;
        this.addedDate = addedDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = normalizeIsbn(isbn);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSoLuongKhaDung() {
        return soLuongKhaDung;
    }

    public void setSoLuongKhaDung(Integer soLuongKhaDung) {
        this.soLuongKhaDung = soLuongKhaDung;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    // Selection helpers (UI only)
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Utility: check if addedDate (ISO string) is older than given days from now.
     * If addedDate is null or unparsable, returns false.
     */
    public boolean isAddedDateOlderThanDays(int days) {
        if (addedDate == null || addedDate.isEmpty()) return false;
        try {
            // Expecting ISO 8601 like 2024-08-28T00:00:00.000Z
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date ad = sdf.parse(addedDate);
            if (ad == null) return false;
            long diff = new java.util.Date().getTime() - ad.getTime();
            long daysDiff = diff / (24L * 60L * 60L * 1000L);
            return daysDiff > days;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCartResponse that = (BookCartResponse) o;
        String a = this.isbn != null ? this.isbn.trim() : null;
        String b = that.isbn != null ? that.isbn.trim() : null;
        return a != null && a.equals(b);
    }

    @Override
    public int hashCode() {
        return isbn != null ? isbn.trim().hashCode() : 0;
    }
}