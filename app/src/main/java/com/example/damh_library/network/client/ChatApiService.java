package com.example.damh_library.network.client;

import com.example.damh_library.model.request.ChatRequest;
import com.example.damh_library.model.response.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatApiService {
    
    @Headers({
        "Content-Type: application/json",
        "ngrok-skip-browser-warning: true"
    })
    @POST("api/library/chat")
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}
