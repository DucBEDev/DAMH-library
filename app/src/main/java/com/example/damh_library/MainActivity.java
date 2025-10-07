package com.example.damh_library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilUserId;
    private TextInputLayout tilPassword;
    private TextInputEditText edtUserId;
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
        tilUserId = findViewById(R.id.tilUserId);
        tilPassword = findViewById(R.id.tilPassword);
        edtUserId = findViewById(R.id.edtUserId);
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

        String userId = edtUserId.getText().toString().trim();
        if (userId.isEmpty()) {
            tilUserId.setError("Vui lòng nhập id");
            isValid = false;
        } else {
            tilUserId.setError(null);
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
        String userId = edtUserId.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (isCustomerMode) {
            loginCustomer(userId, password);
        } else {
            loginManager(userId, password);
        }
    }

    private void loginCustomer(String email, String password) {
        // TODO: Gọi API đăng nhập khách hàng
        // Ví dụ mẫu:
//        if (email.equals("customer@library.com") && password.equals("123456")) {
//            Toast.makeText(this, "Đăng nhập khách hàng thành công!", Toast.LENGTH_SHORT).show();
//
//            // Chuyển đến màn hình chính của khách hàng
//            Intent intent = new Intent(LoginActivity.this, CustomerMainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        } else {
//            Toast.makeText(this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
//        }
    }

    private void loginManager(String email, String password) {
        // TODO: Gọi API đăng nhập quản lý
        // Ví dụ mẫu:
//        if (email.equals("manager@library.com") && password.equals("admin123")) {
//            Toast.makeText(this, "Đăng nhập quản lý thành công!", Toast.LENGTH_SHORT).show();
//
//            // Chuyển đến màn hình quản lý
//            Intent intent = new Intent(LoginActivity.this, ManagerMainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        } else {
//            Toast.makeText(this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
//        }
    }
}