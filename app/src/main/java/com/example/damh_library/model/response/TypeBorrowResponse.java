package com.example.damh_library.model.response;

public class TypeBorrowResponse {
    private String type;
    private int borrowCount;

    public TypeBorrowResponse() { }

    public TypeBorrowResponse(String type, int borrowCount) {
        this.type = type;
        this.borrowCount = borrowCount;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getBorrowCount() {
        return borrowCount;
    }
    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }
}

