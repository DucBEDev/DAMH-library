package com.example.damh_library.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.damh_library.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputLayout tilBirthDate, tilAddress, tilPhone, tilIdCard;
    private TextInputEditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    private TextInputEditText edtBirthDate, edtAddress, edtPhone, edtIdCard;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private CheckBox cbTerms;
    private MaterialButton btnRegister;
    private TextView tvLogin;

    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilBirthDate = findViewById(R.id.tilBirthDate);
        tilAddress = findViewById(R.id.tilAddress);
        tilPhone = findViewById(R.id.tilPhone);
        tilIdCard = findViewById(R.id.tilIdCard);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtIdCard = findViewById(R.id.edtIdCard);

        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    performRegister();
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        edtBirthDate.setText(dateFormatter.format(calendar.getTime()));
                    }
                },
                year, month, day
        );

        // Giới hạn ngày sinh (phải trên 16 tuổi)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -16);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Họ và tên
        String fullName = edtFullName.getText().toString().trim();
        if (fullName.isEmpty()) {
            tilFullName.setError("Vui lòng nhập họ và tên");
            isValid = false;
        } else if (fullName.length() < 3) {
            tilFullName.setError("Họ và tên phải có ít nhất 3 ký tự");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

        // Validate Email
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validate Password
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

        // Validate Confirm Password
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        // Validate Ngày sinh
        String birthDate = edtBirthDate.getText().toString().trim();
        if (birthDate.isEmpty()) {
            tilBirthDate.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        } else {
            tilBirthDate.setError(null);
        }

        // Validate Địa chỉ
        String address = edtAddress.getText().toString().trim();
        if (address.isEmpty()) {
            tilAddress.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        } else if (address.length() < 10) {
            tilAddress.setError("Địa chỉ phải có ít nhất 10 ký tự");
            isValid = false;
        } else {
            tilAddress.setError(null);
        }

        // Validate Số điện thoại
        String phone = edtPhone.getText().toString().trim();
        Pattern phonePattern = Pattern.compile("^[0-9]{10}$");
        if (phone.isEmpty()) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        } else if (!phonePattern.matcher(phone).matches()) {
            tilPhone.setError("Số điện thoại không hợp lệ (10 số)");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        // Validate CMND/CCCD
        String idCard = edtIdCard.getText().toString().trim();
        Pattern idPattern = Pattern.compile("^[0-9]{9,12}$");
        if (idCard.isEmpty()) {
            tilIdCard.setError("Vui lòng nhập số CMND/CCCD");
            isValid = false;
        } else if (!idPattern.matcher(idCard).matches()) {
            tilIdCard.setError("Số CMND/CCCD không hợp lệ (9-12 số)");
            isValid = false;
        } else {
            tilIdCard.setError(null);
        }

        // Validate Checkbox điều khoản
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản và chính sách", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void performRegister() {
        // Lấy tất cả thông tin
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String birthDate = edtBirthDate.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String idCard = edtIdCard.getText().toString().trim();

        // Lấy giới tính
        String gender = "";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rbMale) {
            gender = "Nam";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Nữ";
        }

        // TODO: Gọi API để đăng ký
        // Ví dụ mẫu:
        Toast.makeText(this, "Đăng ký thành công!\nChào mừng " + fullName, Toast.LENGTH_LONG).show();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}