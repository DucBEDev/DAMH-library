package com.example.damh_library.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.damh_library.model.request.PhieuMuonPaymentRequest;
import com.example.damh_library.utils.PhieuMuonPaymentHelper;

import java.util.ArrayList;
import java.util.List;

public class PhieuMuonPaymentActivity extends AppCompatActivity {
    
    private Button btnMuonSach;
    private ProgressBar progressBar;
    private TextView tvStatus;
    
    private PhieuMuonPaymentHelper paymentHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_phieu_muon_payment);
        
        // Initialize views
        // btnMuonSach = findViewById(R.id.btn_muon_sach);
        // progressBar = findViewById(R.id.progress_bar);
        // tvStatus = findViewById(R.id.tv_status);
        
        // Initialize payment helper
        paymentHelper = new PhieuMuonPaymentHelper(this);
        
        // Setup button click
        setupButtonListener();
    }
    
    private void setupButtonListener() {
        if (btnMuonSach != null) {
            btnMuonSach.setOnClickListener(v -> startPhieuMuonWithPayment());
        }
    }

    private void startPhieuMuonWithPayment() {
        // Show loading
        showLoading(true);
        updateStatus("Đang tạo yêu cầu thanh toán...");
        
        // Get reader ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String userIdStr = prefs.getString("key_userId", "");

        int maDG = 0;
        try {
            maDG = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Không tìm thấy thông tin độc giả", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        boolean hinhThuc = true; // true = mang về, false = tại chỗ
        int maNV = 4; // Staff ID
        int amount = 50000; // Fee amount in VND
        
        // Create list of books
        List<PhieuMuonPaymentRequest.SachItem> danhSachSach = new ArrayList<>();
        danhSachSach.add(new PhieuMuonPaymentRequest.SachItem("S00001", true));
        danhSachSach.add(new PhieuMuonPaymentRequest.SachItem("S00002", true));
        
        // Create request
        PhieuMuonPaymentRequest request = new PhieuMuonPaymentRequest(
            maDG, hinhThuc, maNV, danhSachSach, amount
        );
        
        // Start payment flow
        paymentHelper.startPaymentFlow(request, new PhieuMuonPaymentHelper.PaymentCallback() {
            @Override
            public void onSuccess(long orderCode) {
                runOnUiThread(() -> {
                    showLoading(false);
                    updateStatus("Mượn sách thành công!");
                    
                    Toast.makeText(PhieuMuonPaymentActivity.this, 
                        "Mượn sách thành công! OrderCode: " + orderCode, 
                        Toast.LENGTH_LONG).show();
                    
                    try {
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            try {
                                finish();
                            } catch (Exception e) {
                            }
                        }, 1500);
                    } catch (Exception e) {
                        finish();
                    }
                });
            }
            
            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    updateStatus("Lỗi: " + message);
                    
                    Toast.makeText(PhieuMuonPaymentActivity.this, 
                        "Lỗi: " + message, 
                        Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onPaymentPending(String status) {
                runOnUiThread(() -> {
                    updateStatus("Đang chờ thanh toán... (" + status + ")");
                });
            }
        });
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (btnMuonSach != null) {
            btnMuonSach.setEnabled(!show);
        }
    }
    
    private void updateStatus(String message) {
        if (tvStatus != null) {
            tvStatus.setText(message);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop polling when activity is destroyed
        if (paymentHelper != null) {
            paymentHelper.destroy();
        }
    }
}
