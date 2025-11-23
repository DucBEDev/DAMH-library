package com.example.damh_library.network.client;

import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.response.ReaderProfileResponse;
import com.example.damh_library.model.response.ReaderCardResponse;
import com.example.damh_library.model.response.BorrowedBookResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Part;

public interface ReaderApiService {
    @GET("admin/reader/profile-info")
    Call<ResponseSingleModel<ReaderProfileResponse>> getProfileInfo(@Query("id") String id);

    @GET("admin/reader/card-info")
    Call<ResponseSingleModel<ReaderCardResponse>> getCardInfo(@Query("id") String id);

    @GET("admin/reader/borrowed")
    Call<ResponseModel<BorrowedBookResponse>> getBorrowedBooks(@Query("id") String id);

    // Sửa thành PATCH và dùng @Path thay vì @Query
    @Multipart
    @PATCH("admin/reader/update-profile/{maDG}")
    Call<ResponseSingleModel<ReaderProfileResponse>> updateProfile(
        @Path("maDG") String userId,
        @Part("hoTenDG") RequestBody hoTenDG,
        @Part("emailDG") RequestBody emailDG,
        @Part("soCMND") RequestBody soCMND,
        @Part("gioiTinh") RequestBody gioiTinh,
        @Part("ngaySinh") RequestBody ngaySinh,
        @Part("diaChiDG") RequestBody diaChiDG,
        @Part("dienThoai") RequestBody dienThoai,
        @Part("currentImagePath") RequestBody currentImagePath,
        @Part("hasNewImage") RequestBody hasNewImage,
        @Part MultipartBody.Part avatar
    );
}
