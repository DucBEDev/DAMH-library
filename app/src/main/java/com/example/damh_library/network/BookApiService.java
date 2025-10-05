package com.example.damh_library.network;

import com.example.damh_library.model.Book;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BookApiService {

    @GET("/books")
    Call<List<Book>> getBooks();
}
