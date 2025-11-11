package com.example.damh_library.network.admin;

import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.ForgotPasswordRequest;
import com.example.damh_library.model.request.LoginRequest;
import com.example.damh_library.model.request.RegisterRequest;
import com.example.damh_library.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("admin/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("admin/auth/register")
    Call<ResponseSingleModel<Object>> register(@Body RegisterRequest request);

    @POST("admin/auth/forgot-password")
    Call<ResponseSingleModel<Object>> forgotPassword(@Body ForgotPasswordRequest request);
}
