package com.example.damh_library.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.damh_library.R;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.UpdatePasswordRequest;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.admin.AuthApiService;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterNewPassword extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnConfirm, btnCancel;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_new_password);

        // Nhận email từ Intent
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }

        initViews();
        setupActions();
    }



    private void initViews() {
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);

        // Ẩn mật khẩu mặc định
        edtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void setupActions() {
        btnConfirm.setOnClickListener(v -> {
            if (validatePassword()) {
                String newPassword = edtNewPassword.getText().toString().trim();
                // TODO: Gọi API đổi mật khẩu ở đây
                updatePassword(newPassword);

                Toasty.success(this, "Đổi mật khẩu thành công!", Toasty.LENGTH_LONG).show();
                finish(); // Quay về màn hình login
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private boolean validatePassword() {
        String password = edtNewPassword.getText().toString().trim();
        String confirm = edtConfirmPassword.getText().toString().trim();

        if (password.isEmpty()) {
            Toasty.warning(this, "Vui lòng nhập mật khẩu mới", Toasty.LENGTH_SHORT).show();
            edtNewPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            Toasty.warning(this, "Mật khẩu phải có ít nhất 6 ký tự", Toasty.LENGTH_SHORT).show();
            edtNewPassword.requestFocus();
            return false;
        }

        if (confirm.isEmpty()) {
            Toasty.warning(this, "Vui lòng xác nhận mật khẩu", Toasty.LENGTH_SHORT).show();
            edtConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirm)) {
            Toasty.error(this, "Mật khẩu xác nhận không khớp", Toasty.LENGTH_SHORT).show();
            edtConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void updatePassword(String newPassword) {

        if (newPassword == null || newPassword.isEmpty()) {
            Toasty.error(this, "Mật khẩu mới không được để trống", Toasty.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toasty.error(this, "Mật khẩu phải có ít nhất 6 ký tự", Toasty.LENGTH_SHORT).show();
            return;
        }

        // Tạo request body
        UpdatePasswordRequest request = new UpdatePasswordRequest(email, newPassword);

        AuthApiService service = ApiClient.getClient().create(AuthApiService.class);
        Call<ResponseSingleModel<Object>> call = service.updatePassword(request);

        call.enqueue(new Callback<ResponseSingleModel<Object>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<Object>> call, Response<ResponseSingleModel<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toasty.success(EnterNewPassword.this,
                                "Cập nhật mật khẩu thành công!", Toasty.LENGTH_LONG).show();

                        // Quay về màn hình login hoặc dashboard
                        Intent intent = new Intent(EnterNewPassword.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toasty.error(EnterNewPassword.this,
                                response.body().getMessage() != null ? response.body().getMessage() : "Cập nhật thất bại",
                                Toasty.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(EnterNewPassword.this,
                            "Lỗi server, vui lòng thử lại sau.", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<Object>> call, Throwable t) {
                Toasty.error(EnterNewPassword.this,
                        "Lỗi kết nối: " + t.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }


}