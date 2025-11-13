package com.example.damh_library.network.client;

import com.example.damh_library.model.response.TypeBorrowResponse;
import com.example.damh_library.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookTypeApiService {
    @GET("admin/type/most-borrowed")
    Call<ResponseModel<TypeBorrowResponse>> getMostBorrowedTypes(@Query("limit") int limit);
}

