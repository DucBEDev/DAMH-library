package com.example.damh_library.network.client;

import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.PhieuMuonRequest;
import com.example.damh_library.model.response.CheckoutHistoryResponse;
import com.example.damh_library.model.response.OverdueCheckResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CheckoutSlipApiService {
    @GET("admin/phieumuon/{readerId}")
    Call<ResponseModel<CheckoutHistoryResponse>> getReaderCheckoutHistory(@Path("readerId") String readerId);

    @GET("admin/phieumuon/{readerId}")
    Call<ResponseModel<CheckoutHistoryResponse>> searchCheckoutHistory(
        @Path("readerId") String readerId,
        @Query("maPhieu") String maPhieu
    );

    @POST("admin/phieumuon/create")
    Call<ResponseModel<Void>> createCheckoutWithRequest(@Body PhieuMuonRequest request);

    // Trong DauSachApiService hoặc AuthApiService
    @GET("admin/phieumuon/auto-return-overdue-ebooks")
    Call<ResponseSingleModel<Integer>> autoReturnOverdueEbooks();

    @POST("admin/phieumuon/{readerId}")
    Call<ResponseModel<CheckoutHistoryResponse>> createCheckout(
        @Path("readerId") String readerId,
        @Query("maPhieu") String maPhieu
    );

    @GET("admin/phieumuon/check-status/{maDG}")
    Call<ResponseSingleModel<OverdueCheckResponse>> checkReaderBorrowStatus(@Path("maDG") String maDG);
}

