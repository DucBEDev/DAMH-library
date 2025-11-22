package com.example.damh_library.network.client;

import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.response.BookCartResponse;
import com.example.damh_library.model.response.BookDetailResponse;
import com.example.damh_library.model.response.MostBorrowBookResponse;
import com.example.damh_library.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DauSachApiService {
    @GET("admin/dausach/most-borrowed")
    Call<ResponseModel<MostBorrowBookResponse>> getMostBorrowed(@Query("limit") int limit);

    @GET("admin/dausach/most-quantity")
    Call<ResponseModel<MostBorrowBookResponse>> getMostQuantity(@Query("limit") int limit);

    @GET("admin/dausach/favorites/{readerId}")
    Call<ResponseModel<BookCartResponse>> getFavoriteBooks(@Path("readerId") String readerId);

    @GET("admin/dausach/detail/{isbn}")
    Call<ResponseSingleModel<BookDetailResponse>> getBookDetail(@Path("isbn") String isbn);
}
