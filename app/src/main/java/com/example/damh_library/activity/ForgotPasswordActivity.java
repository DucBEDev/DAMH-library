package com.example.damh_library.activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.damh_library.R;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.ForgotPasswordRequest;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.admin.AuthApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText edtEmail;
    private MaterialButton btnSubmit, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        tilEmail = findViewById(R.id.tilEmail);
        edtEmail = findViewById(R.id.edtEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForgotPassword();
            }
        });
    }

    private boolean validate() {
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            return false;
        }
        tilEmail.setError(null);
        return true;
    }

    private void submitForgotPassword() {
        if (!validate()) return;

        String email = edtEmail.getText().toString().trim();
        AuthApiService service = ApiClient.getClient().create(AuthApiService.class);
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        Call<ResponseSingleModel<Object>> call = service.forgotPassword(request);
        call.enqueue(new Callback<ResponseSingleModel<Object>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<Object>> call, Response<ResponseSingleModel<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseSingleModel<Object> body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Yêu cầu đổi mật khẩu đã được gửi. Vui lòng kiểm tra email.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi server, vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<Object>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

