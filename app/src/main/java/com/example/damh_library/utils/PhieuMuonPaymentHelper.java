package com.example.damh_library.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;

import com.example.damh_library.model.response.PaymentResponse;
import com.example.damh_library.model.request.PhieuMuonPaymentRequest;
import com.example.damh_library.model.response.VerifyPaymentResponse;
import com.example.damh_library.network.PaymentApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhieuMuonPaymentHelper {
    
    private static final String BASE_URL = "http://10.0.2.2:3000/Library/admin/payment/";
    private static final int POLLING_INTERVAL = 3000;
    private static final int MAX_POLLING_ATTEMPTS = 60;
    private static final int MAX_CONSECUTIVE_ERRORS = 5;
    
    private Context context;
    private PaymentApiService apiService;
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private int pollingAttempts = 0;
    private int consecutiveErrors = 0;
    private boolean isDestroyed = false;
    
    public interface PaymentCallback {
        void onSuccess(long orderCode);
        void onFailure(String message);
        void onPaymentPending(String status);
    }
    
    public PhieuMuonPaymentHelper(Context context) {
        this.context = context;
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(PaymentApiService.class);
        pollingHandler = new Handler(Looper.getMainLooper());
    }

    public void startPaymentFlow(PhieuMuonPaymentRequest request, PaymentCallback callback) {
        apiService.createPhieuMuonRequest(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();
                    
                    if (paymentResponse.isSuccess()) {
                        String checkoutUrl = paymentResponse.getData().getCheckoutUrl();
                        long orderCode = paymentResponse.getData().getOrderCode();
                        
                        openPaymentUrl(checkoutUrl);
                        startPolling(orderCode, callback);
                        
                    } else {
                        callback.onFailure(paymentResponse.getMessage());
                    }
                } else {
                    callback.onFailure("Failed to create payment request");
                }
            }
            
            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    private void openPaymentUrl(String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(true);
            builder.setUrlBarHidingEnabled(true);
            
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
            
        } catch (Exception e) {
            Toast.makeText(context, "Không thể mở trang thanh toán", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void startPolling(long orderCode, PaymentCallback callback) {
        pollingAttempts = 0;
        consecutiveErrors = 0;
        
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (isDestroyed || pollingHandler == null) {
                    return;
                }
                
                if (pollingAttempts >= MAX_POLLING_ATTEMPTS) {
                    callback.onFailure("Timeout: Không nhận được kết quả thanh toán sau 3 phút");
                    return;
                }
                
                pollingAttempts++;
                
                // Call verify API
                apiService.verifyAndCreatePhieuMuon(orderCode).enqueue(new Callback<VerifyPaymentResponse>() {
                    @Override
                    public void onResponse(Call<VerifyPaymentResponse> call, Response<VerifyPaymentResponse> response) {
                        // Reset consecutive errors on successful response
                        consecutiveErrors = 0;
                        
                        if (response.isSuccessful() && response.body() != null) {
                            VerifyPaymentResponse verifyResponse = response.body();
                            String status = verifyResponse.getPaymentStatus();
                            
                            if ("PAID".equals(status) && verifyResponse.isPhieuMuonCreated()) {
                                stopPolling();
                                
                                try {
                                    callback.onSuccess(orderCode);
                                } catch (Exception e) {
                                }
                                
                            } else if ("CANCELLED".equals(status) || "EXPIRED".equals(status)) {
                                stopPolling();
                                callback.onFailure("Thanh toán " + status);
                                
                            } else {
                                callback.onPaymentPending(status);
                                
                                if (!isDestroyed && pollingHandler != null) {
                                    pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
                                }
                            }
                        } else {
                            if (!isDestroyed && pollingHandler != null) {
                                pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<VerifyPaymentResponse> call, Throwable t) {
                        consecutiveErrors++;
                        
                        if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                            stopPolling();
                            callback.onFailure("Lỗi mạng liên tục. Vui lòng kiểm tra kết nối internet và thử lại.");
                            return;
                        }
                        
                        if (!isDestroyed && pollingHandler != null) {
                            pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
                        }
                    }
                });
            }
        };
        
        pollingHandler.post(pollingRunnable);
    }
    
    public void stopPolling() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }
    
    public void destroy() {
        isDestroyed = true;
        stopPolling();
        pollingHandler = null;
        pollingRunnable = null;
        context = null;
    }
}
