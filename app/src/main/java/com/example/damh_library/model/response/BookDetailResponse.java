package com.example.damh_library.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookDetailResponse {

    @SerializedName("isbn")
    private String isbn;

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("type")
    private String type; // Thể loại

    @SerializedName("language")
    private String language;

    @SerializedName("quantity")
    private int quantity; // Tổng số bản

    @SerializedName("availableQuantity")
    private int availableQuantity; // Số bản còn lại (khả dụng)

    @SerializedName("description")
    private String description;

    @SerializedName("publishDate")
    private String publishDate; // ISO string hoặc null

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("numberOfPages")
    private Integer numberOfPages; // Có thể null

    @SerializedName("edition")
    private Integer edition; // Có thể null

    @SerializedName("price")
    private Long price; // bigint → dùng Long

    @SerializedName("imagePath")
    private String imagePath;

    @SerializedName("bookCopies")
    private List<BookCopy> bookCopies; // Danh sách các bản sách chi tiết

    // Constructor rỗng cho Gson
    public BookDetailResponse() {}

    public BookDetailResponse(String isbn, String title, String author, String type, String language, int quantity, int availableQuantity, String description, String publishDate, String publisher, Integer numberOfPages, Integer edition, Long price, String imagePath, List<BookCopy> bookCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.type = type;
        this.language = language;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.description = description;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.numberOfPages = numberOfPages;
        this.edition = edition;
        this.price = price;
        this.imagePath = imagePath;
        this.bookCopies = bookCopies;
    }

    // Getter và Setter
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getNumberOfPages() { return numberOfPages; }
    public void setNumberOfPages(Integer numberOfPages) { this.numberOfPages = numberOfPages; }

    public Integer getEdition() { return edition; }
    public void setEdition(Integer edition) { this.edition = edition; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) {  this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public List<BookCopy> getBookCopies() { return bookCopies; }
    public void setBookCopies(List<BookCopy> bookCopies) { this.bookCopies = bookCopies; }



    // Inner class cho từng bản sách
    public static class BookCopy {
        @SerializedName("MASACH")
        private String maSach;

        @SerializedName("TINHTRANG")
        private boolean tinhTrangSach; // true = tốt

        @SerializedName("CHOMUON")
        private boolean dangMuon; // true = đang được mượn

        public BookCopy() {}

        public BookCopy(String maSach, boolean tinhTrangSach, boolean dangMuon) {
            this.maSach = maSach;
            this.tinhTrangSach = tinhTrangSach;
            this.dangMuon = dangMuon;
        }

        public String getMaSach() { return maSach; }
        public void setMaSach(String maSach) { this.maSach = maSach; }

        public boolean isTinhTrangSach() { return tinhTrangSach; }
        public void setTinhTrangSach(boolean tinhTrangSach) { this.tinhTrangSach = tinhTrangSach; }

        public boolean isDangMuon() { return dangMuon; }
        public void setDangMuon(boolean dangMuon) { this.dangMuon = dangMuon; }

        @Override
        public String toString() {
            return "BookCopy{" +
                    "maSach='" + maSach + '\'' +
                    ", tinhTrangSach=" + tinhTrangSach +
                    ", dangMuon=" + dangMuon +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BookDetailResponse{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", language='" + language + '\'' +
                ", quantity=" + quantity +
                ", availableQuantity=" + availableQuantity +
                ", description='" + description + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", numberOfPages=" + numberOfPages +
                ", edition=" + edition +
                ", price=" + price +
                ", imagePath='" + imagePath + '\'' +
                ", bookCopies=" + bookCopies +
                '}';
    }
}