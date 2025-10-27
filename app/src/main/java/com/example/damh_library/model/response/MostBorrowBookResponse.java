package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

public class MostBorrowBookResponse {
    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("borrowCount")
    private int borrowCount;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("imagePath")
    private String imagePath;

    @SerializedName("type")
    private String type;

    public MostBorrowBookResponse() {
        // no-arg constructor for deserialization
    }

    public MostBorrowBookResponse(String title, String author, int borrowCount, int quantity) {
        this.title = title;
        this.author = author;
        this.borrowCount = borrowCount;
        this.quantity = quantity;
        this.imagePath = null;
    }

    // New constructor used when API provides imagePath
    public MostBorrowBookResponse(String title, String author, int borrowCount, String imagePath) {
        this.title = title;
        this.author = author;
        this.borrowCount = borrowCount;
        this.quantity = 0;
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", borrowCount=" + borrowCount +
                ", quantity=" + quantity +
                ", imagePath='" + imagePath + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
