package com.example.damh_library.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.damh_library.R;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.ForgotPasswordRequest;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.admin.AuthApiService;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterOtpActivity extends AppCompatActivity {

    private EditText[] otpEditTexts = new EditText[6];
    private TextView tvResendOtp;
    private Button btnVerifyOtp, btnCancel;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_otp);

        // Nhận email từ Intent
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }

        initViews();
        setupOtpInput();
        setupActions();
    }

    private void initViews() {
        otpEditTexts[0] = findViewById(R.id.edtOtp1);
        otpEditTexts[1] = findViewById(R.id.edtOtp2);
        otpEditTexts[2] = findViewById(R.id.edtOtp3);
        otpEditTexts[3] = findViewById(R.id.edtOtp4);
        otpEditTexts[4] = findViewById(R.id.edtOtp5);
        otpEditTexts[5] = findViewById(R.id.edtOtp6);

        tvResendOtp = findViewById(R.id.tvResendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupOtpInput() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final int index = i;

            otpEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        // Chuyển focus sang ô tiếp theo
                        if (index < otpEditTexts.length - 1) {
                            otpEditTexts[index + 1].requestFocus();
                        } else {
                            // Đã nhập đủ 6 số → có thể tự động xác nhận
                            btnVerifyOtp.requestFocus();
                        }
                    } else if (s.length() == 0) {
                        // Xóa → chuyển về ô trước nếu có
                        if (index > 0) {
                            otpEditTexts[index - 1].requestFocus();
                        }
                    }
                }
            });

            // Xử lý nút back khi ô trống
            otpEditTexts[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && otpEditTexts[index].getText().length() == 0) {
                    if (index > 0) {
                        otpEditTexts[index - 1].requestFocus();
                        otpEditTexts[index - 1].setText("");
                    }
                }
                return false;
            });
        }

        // Focus ô đầu tiên khi mở
        otpEditTexts[0].requestFocus();
    }

    private void setupActions() {
        // Gửi lại mã OTP
        tvResendOtp.setOnClickListener(v -> {
            Toasty.info(this, "Đang gửi lại mã OTP...", Toasty.LENGTH_SHORT).show();
            // TODO: Gọi API gửi lại OTP
            submitForgotPassword(email);
        });

        // Xác nhận OTP
        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = getOtpFromInputs();

            if (enteredOtp.length() != 6) {
                Toasty.error(this, "Vui lòng nhập đầy đủ 6 chữ số mã OTP", Toasty.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("otp_session", Context.MODE_PRIVATE);

            // Lấy dữ liệu đã lưu
            String savedOtp = prefs.getString("otp_code", null);
            String savedEmail = prefs.getString("otp_email", null);
            long savedTimestamp = prefs.getLong("otp_timestamp", 0);

            // Kiểm tra xem có dữ liệu OTP lưu không
            if (savedOtp == null || savedEmail == null || savedTimestamp == 0) {
                Toasty.error(this, "Không tìm thấy mã OTP. Vui lòng gửi lại mã.", Toasty.LENGTH_LONG).show();
                return;
            }

            // Kiểm tra thời gian hết hạn (ví dụ: 5 phút = 300000 milliseconds)
            long currentTime = System.currentTimeMillis();
            long otpValidityDuration = 5 * 60 * 1000; // 5 phút
            boolean isOtpExpired = (currentTime - savedTimestamp) > otpValidityDuration;

            if (isOtpExpired) {
                Toasty.error(this, "Mã OTP đã hết hạn. Vui lòng gửi lại mã mới.", Toasty.LENGTH_LONG).show();
                // Tùy chọn: Xóa OTP cũ
                clearOtpSession();
                return;
            }

            // Kiểm tra mã OTP có khớp không
            if (enteredOtp.equals(savedOtp)) {
                Toasty.success(this, "Xác nhận OTP thành công!", Toasty.LENGTH_SHORT).show();

                // Xóa OTP sau khi xác nhận thành công (bảo mật)
                clearOtpSession();

                // Chuyển sang màn hình đặt lại mật khẩu
                Intent intent = new Intent(EnterOtpActivity.this, EnterNewPassword.class);
                intent.putExtra("email", savedEmail); // Truyền email đã lưu
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toasty.error(this, "Mã OTP không đúng. Vui lòng thử lại.", Toasty.LENGTH_SHORT).show();
            }
        });
        // Hủy
        btnCancel.setOnClickListener(v -> finish());
    }

    private String getOtpFromInputs() {
        StringBuilder otp = new StringBuilder();
        for (EditText editText : otpEditTexts) {
            String digit = editText.getText().toString().trim();
            if (!digit.isEmpty()) {
                otp.append(digit);
            }
        }
        return otp.toString();
    }

    private void clearOtpSession() {
        SharedPreferences prefs = getSharedPreferences("otp_session", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private void saveOtp(String otp, String email) {
        SharedPreferences prefs = getSharedPreferences("otp_session", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("otp_code", otp)
                .putString("otp_email", email)
                .putLong("otp_timestamp", System.currentTimeMillis()) // Thời gian lưu
                .apply();
    }
    private void submitForgotPassword(String email) {

        if (email == null || email.isEmpty()) {
            Toast.makeText(EnterOtpActivity.this, "Email không hợp lệ", Toast.LENGTH_LONG).show();
            return;
        }

//        String email = edtEmail.getText().toString().trim();
        AuthApiService service = ApiClient.getClient().create(AuthApiService.class);
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        Call<ResponseSingleModel<Object>> call = service.forgotPassword(request);
        call.enqueue(new Callback<ResponseSingleModel<Object>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<Object>> call, Response<ResponseSingleModel<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseSingleModel<Object> body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(EnterOtpActivity.this, "Yêu cầu đổi mật khẩu đã được gửi. Vui lòng kiểm tra email.", Toast.LENGTH_LONG).show();
                        saveOtp((String) body.getData(), email);
                    } else {
                        Toast.makeText(EnterOtpActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EnterOtpActivity.this, "Lỗi server, vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<Object>> call, Throwable t) {
                Toast.makeText(EnterOtpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}