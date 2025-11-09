package com.example.damh_library.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.damh_library.R;
import com.example.damh_library.activity.client.Client_dashboard;
import com.example.damh_library.model.request.LoginRequest;
import com.example.damh_library.model.response.LoginResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.admin.AuthApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilUserName;
    private TextInputLayout tilPassword;
    private TextInputEditText edtUserName;
    private TextInputEditText edtPassword;
    private RadioGroup rgUserType;
    private RadioButton rbCustomer;
    private RadioButton rbManager;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegister;
    private LinearLayout llRegister;

    private boolean isCustomerMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilUserName = findViewById(R.id.tilUserName);
        tilPassword = findViewById(R.id.tilPassword);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        rgUserType = findViewById(R.id.rgUserType);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbManager = findViewById(R.id.rbManager);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        llRegister = findViewById(R.id.llRegister);
    }

    private void setupListeners() {
        rgUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbCustomer) {
                    isCustomerMode = true;
                    showCustomerUI();
                } else if (checkedId == R.id.rbManager) {
                    isCustomerMode = false;
                    showManagerUI();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    performLogin();
                }
            }
        });

        // Xử lý quên mật khẩu
//        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Chuyển đến màn hình quên mật khẩu
//                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//                startActivity(intent);
//            }
//        });
//
        // Xử lý đăng ký
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showCustomerUI() {
        tvForgotPassword.setVisibility(View.VISIBLE);
        llRegister.setVisibility(View.VISIBLE);
    }

    private void showManagerUI() {
        tvForgotPassword.setVisibility(View.GONE);
        llRegister.setVisibility(View.GONE);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String userName = edtUserName.getText().toString().trim();
        if (userName.isEmpty()) {
            tilUserName.setError("Vui lòng nhập username");
            isValid = false;
        } else {
            tilUserName.setError(null);
        }

        String password = edtPassword.getText().toString().trim();
        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    private void performLogin() {
        String userName = edtUserName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (isCustomerMode) {
            loginAction(userName, password, "reader");
        } else {
            loginAction(userName, password, "librarian");
        }
    }

    private void loginAction(String userName, String password, String type) {
        AuthApiService apiService = ApiClient.getClient().create(AuthApiService.class);
        LoginRequest request = new LoginRequest(userName, password);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString("key_username", loginResponse.getUserName() != null ? loginResponse.getUserName() : "")
                                .putString("key_userEmail", loginResponse.getUserEmail() != null ? loginResponse.getUserEmail() : "")
                                .putString("key_userType", loginResponse.getUserType() != null ? loginResponse.getUserType() : "")
                                .putString("key_userId", loginResponse.getUserId() != null ? loginResponse.getUserId() : "")
                                .putString("key_userPhone", loginResponse.getUserPhone() != null ? loginResponse.getUserPhone() : "")
                                .apply();

                        Intent intent;

                        if (type.equals("reader") && loginResponse.getUserType().equals("reader")) {
                            Toasty.success(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT, true).show();
                            intent = new Intent(MainActivity.this, Client_dashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else if (type.equals("librarian") && loginResponse.getUserType().equals("librarian")) {
                            Toasty.success(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT, true).show();
                            intent = new Intent(MainActivity.this, Client_dashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toasty.error(MainActivity.this, "Bạn không có tài khoản hoặc quyền truy cập", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(MainActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(MainActivity.this, "Đăng nhập thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toasty.error(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}