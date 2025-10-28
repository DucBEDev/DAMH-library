package com.example.damh_library.network.client;

import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.response.ReaderProfileResponse;
import com.example.damh_library.model.request.UpdateClientProfileRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReaderApiService {
    @GET("admin/reader/profile-info")
    Call<ResponseSingleModel<ReaderProfileResponse>> getProfileInfo(@Query("id") int id);

    @PATCH("admin/reader/update-profile/{id}")
    Call<ResponseSingleModel<ReaderProfileResponse>> updateProfile(@Path("id") int id, @Body UpdateClientProfileRequest request);
}
