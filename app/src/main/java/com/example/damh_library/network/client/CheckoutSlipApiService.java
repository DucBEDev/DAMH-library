package com.example.damh_library.network.client;

import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.PhieuMuonRequest;
import com.example.damh_library.model.request.UpdateClientProfileRequest;
import com.example.damh_library.model.response.BookCartResponse;
import com.example.damh_library.model.response.CheckoutHistoryResponse;
import com.example.damh_library.model.response.ReaderProfileResponse;
import com.example.damh_library.model.response.TypeBorrowResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CheckoutSlipApiService {
    @GET("admin/phieumuon/{readerId}")
    Call<ResponseModel<CheckoutHistoryResponse>> getReaderCheckoutHistory(@Path("readerId") String readerId);

    @POST("admin/phieumuon/create")
    Call<ResponseModel<Void>> createCheckout(@Body PhieuMuonRequest request);
}

