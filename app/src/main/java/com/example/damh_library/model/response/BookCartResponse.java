package com.example.damh_library.model.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class BookCartResponse implements Parcelable {

    @SerializedName("ISBN")
    private String isbn;

    @SerializedName("maSach")
    private String maSach;

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

    @SerializedName("bookStatus")
    private boolean bookStatus;

    // Constructors
    public BookCartResponse() {
    }

    public BookCartResponse(String isbn, String maSach, String title, String author, String publisher,
                            String imageUrl, Integer soLuongKhaDung, String addedDate, boolean bookStatus) {
        this.isbn = normalizeIsbn(isbn);
        this.maSach = normalizeCode(maSach);
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.soLuongKhaDung = soLuongKhaDung;
        this.bookStatus = bookStatus;
        this.addedDate = addedDate;
    }

    private String normalizeIsbn(String raw) {
        if (raw == null) return null;
        return raw.trim();
    }

    private String normalizeCode(String raw) {
        if (raw == null) return null;
        return raw.trim();
    }

    public BookCartResponse(String isbn, String maSach, String title, String author, String publisher, Integer soLuongKhaDung, String addedDate, boolean bookStatus) {
        this.isbn = isbn;
        this.maSach = maSach;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.soLuongKhaDung = soLuongKhaDung;
        this.addedDate = addedDate;
        this.bookStatus = bookStatus;
    }

    // Parcelable implementation
    protected BookCartResponse(Parcel in) {
        isbn = in.readString();
        maSach = in.readString();
        title = in.readString();
        author = in.readString();
        publisher = in.readString();
        imageUrl = in.readString();
        if (in.readByte() == 0) {
            soLuongKhaDung = null;
        } else {
            soLuongKhaDung = in.readInt();
        }
        addedDate = in.readString();
    }

    public static final Creator<BookCartResponse> CREATOR = new Creator<BookCartResponse>() {
        @Override
        public BookCartResponse createFromParcel(Parcel in) {
            return new BookCartResponse(in);
        }

        @Override
        public BookCartResponse[] newArray(int size) {
            return new BookCartResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(isbn);
        dest.writeString(maSach);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(publisher);
        dest.writeString(imageUrl);
        if (soLuongKhaDung == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(soLuongKhaDung);
        }
        dest.writeString(addedDate);
    }

    // Getter và Setter (giữ nguyên như code cũ của bạn)
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
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

    public boolean isBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(boolean bookStatus) {
        this.bookStatus = bookStatus;
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        BookCartResponse that = (BookCartResponse) o;
//        String a = this.isbn != null ? this.isbn.trim() : null;
//        String b = that.isbn != null ? that.isbn.trim() : null;
//        return a != null && a.equals(b);
//    }

//    @Override
//    public int hashCode() {
//        return isbn != null ? isbn.trim().hashCode() : 0;
//    }
}