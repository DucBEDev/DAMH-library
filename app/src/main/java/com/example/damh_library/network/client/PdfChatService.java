package com.example.damh_library.network.client;

import com.example.damh_library.model.PdfChatModels;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PdfChatService {
    
    @Headers("ngrok-skip-browser-warning: true")
    @POST("api/pdf/chat")
    Call<PdfChatModels.PdfChatResponse> chatWithPdf(
        @Body PdfChatModels.PdfChatRequest request
    );
}
