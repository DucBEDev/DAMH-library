package com.example.damh_library.network.admin;

import com.example.damh_library.model.request.LoginRequest;
import com.example.damh_library.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("admin/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
