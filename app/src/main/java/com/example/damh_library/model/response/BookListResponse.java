package com.example.damh_library.model.response;

import java.util.List;

// BookListResponse.java
public class BookListResponse {
    private List<MostBorrowBookResponse> books;
    private Pagination pagination;

    public List<MostBorrowBookResponse> getBooks() { return books; }
    public Pagination getPagination() { return pagination; }

    public class Pagination {
        private int currentPage;
        private int pageSize;
        private int totalCount;
        private int totalPages;

        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
        public int getTotalCount() { return totalCount; }
        public int getTotalPages() { return totalPages; }

        @Override
        public String toString() {
            return "Pagination{" +
                    "currentPage=" + currentPage +
                    ", pageSize=" + pageSize +
                    ", totalCount=" + totalCount +
                    ", totalPages=" + totalPages +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BookListResponse{" +
                "books=" + books +
                ", pagination=" + pagination +
                '}';
    }
}


