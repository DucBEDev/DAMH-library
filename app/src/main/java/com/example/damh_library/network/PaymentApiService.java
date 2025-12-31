package com.example.damh_library.network;

import com.example.damh_library.model.response.PaymentResponse;
import com.example.damh_library.model.request.PhieuMuonPaymentRequest;
import com.example.damh_library.model.response.VerifyPaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit API Service for Payment Flow
 * 
 * Base URL: http://10.0.2.2:3000/Library/admin/payment/
 * (10.0.2.2 is localhost for Android Emulator)
 */
public interface PaymentApiService {
    
    /**
     * Step 1: Create Phieu Muon Request with Payment
     * POST /Library/admin/payment/create-phieu-muon-request
     * 
     * @param request Contains maDG, hinhThuc, maNV, danhSachSach, amount
     * @return PaymentResponse with checkoutUrl and orderCode
     */
    @POST("create-phieu-muon-request")
    Call<PaymentResponse> createPhieuMuonRequest(@Body PhieuMuonPaymentRequest request);
    
    /**
     * Step 3 (Polling): Verify Payment and Create Phieu Muon
     * GET /Library/admin/payment/verify-and-create/{orderCode}
     * 
     * This endpoint will:
     * 1. Check PayOS payment status
     * 2. If PAID: Create phieu muon in database
     * 3. Return status for mobile to stop polling
     * 
     * @param orderCode The order code from payment response
     * @return VerifyPaymentResponse with payment status and creation result
     */
    @GET("verify-and-create/{orderCode}")
    Call<VerifyPaymentResponse> verifyAndCreatePhieuMuon(@Path("orderCode") long orderCode);
}
