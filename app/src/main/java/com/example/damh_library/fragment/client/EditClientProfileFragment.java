package com.example.damh_library.fragment.client;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.damh_library.R;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.UpdateClientProfileRequest;
import com.example.damh_library.model.response.ReaderProfileResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.ReaderApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditClientProfileFragment extends Fragment {

    // UI Components
    private FloatingActionButton fabChangeAvatar;
    private TextInputEditText etFullName, etBirthDate, etIdCard;
    private TextInputEditText etEmail, etPhone, etAddress;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private AutoCompleteTextView actvGender;
    private SwitchMaterial switchChangePassword;
    private LinearLayout llPasswordFields;
    private MaterialButton btnCancel, btnSave;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_client_profile, container, false);

        initViews(view);
        setupGenderDropdown();
        setupDatePicker();
        setupPasswordSwitch();
        setupValidation();
        setupButtons();
        loadUserData();

        return view;
    }

    private void initViews(View view) {
        // Avatar
        fabChangeAvatar = view.findViewById(R.id.fabChangeAvatar);

        // Thông tin cá nhân
        etFullName = view.findViewById(R.id.etFullName);
        actvGender = view.findViewById(R.id.actvGender);
        etBirthDate = view.findViewById(R.id.etBirthDate);
        etIdCard = view.findViewById(R.id.etIdCard);

        // Thông tin liên hệ
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);

        // Đổi mật khẩu
        switchChangePassword = view.findViewById(R.id.switchChangePassword);
        llPasswordFields = view.findViewById(R.id.llPasswordFields);
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        tilCurrentPassword = view.findViewById(R.id.tilCurrentPassword);
        tilNewPassword = view.findViewById(R.id.tilNewPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);

        // Buttons
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void setupGenderDropdown() {
        String[] genders = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        actvGender.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etBirthDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etBirthDate.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -10);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setupPasswordSwitch() {
        switchChangePassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                llPasswordFields.setVisibility(View.VISIBLE);
            } else {
                llPasswordFields.setVisibility(View.GONE);
                clearPasswordFields();
            }
        });

        fabChangeAvatar.setOnClickListener(v -> {
            // TODO: Implement avatar selection (gallery/camera)
            Toast.makeText(requireContext(), "Chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupValidation() {
        // Validation cho mật khẩu mới
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && s.length() < 6) {
                    tilNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                } else {
                    tilNewPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validation cho xác nhận mật khẩu
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newPassword = etNewPassword.getText().toString();
                if (!s.toString().equals(newPassword)) {
                    tilConfirmPassword.setError("Mật khẩu không khớp");
                } else {
                    tilConfirmPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validation cho số điện thoại
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && s.length() < 10) {
                    TextInputLayout tilPhone = (TextInputLayout) etPhone.getParent().getParent();
                    tilPhone.setError("Số điện thoại phải có 10 số");
                } else {
                    TextInputLayout tilPhone = (TextInputLayout) etPhone.getParent().getParent();
                    tilPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validation cho email
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    TextInputLayout tilEmail = (TextInputLayout) etEmail.getParent().getParent();
                    tilEmail.setError("Email không hợp lệ");
                } else {
                    TextInputLayout tilEmail = (TextInputLayout) etEmail.getParent().getParent();
                    tilEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(v -> {
            // Quay lại trang profile
            requireActivity().onBackPressed();
        });

        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveProfile();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Kiểm tra họ tên
        if (etFullName.getText().toString().trim().isEmpty()) {
            TextInputLayout tilFullName = (TextInputLayout) etFullName.getParent().getParent();
            tilFullName.setError("Vui lòng nhập họ tên");
            isValid = false;
        }

        // Kiểm tra giới tính
        if (actvGender.getText().toString().trim().isEmpty()) {
            TextInputLayout tilGender = (TextInputLayout) actvGender.getParent().getParent();
            tilGender.setError("Vui lòng chọn giới tính");
            isValid = false;
        }

        // Kiểm tra ngày sinh
        if (etBirthDate.getText().toString().trim().isEmpty()) {
            TextInputLayout tilBirthDate = (TextInputLayout) etBirthDate.getParent().getParent();
            tilBirthDate.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        }

        // Kiểm tra email
        if (etEmail.getText().toString().trim().isEmpty()) {
            TextInputLayout tilEmail = (TextInputLayout) etEmail.getParent().getParent();
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            TextInputLayout tilEmail = (TextInputLayout) etEmail.getParent().getParent();
            tilEmail.setError("Email không hợp lệ");
            isValid = false;
        }

        // Kiểm tra số điện thoại
        if (etPhone.getText().toString().trim().isEmpty()) {
            TextInputLayout tilPhone = (TextInputLayout) etPhone.getParent().getParent();
            tilPhone.setError("Vui lòng nhập số điện tho��i");
            isValid = false;
        } else if (etPhone.getText().toString().length() != 10) {
            TextInputLayout tilPhone = (TextInputLayout) etPhone.getParent().getParent();
            tilPhone.setError("Số điện thoại phải có 10 số");
            isValid = false;
        }

        // Kiểm tra mật khẩu nếu switch được bật
        if (switchChangePassword.isChecked()) {
            String currentPassword = etCurrentPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (currentPassword.isEmpty()) {
                tilCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
                isValid = false;
            }

            if (newPassword.isEmpty()) {
                tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
                isValid = false;
            } else if (newPassword.length() < 6) {
                tilNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                isValid = false;
            }

            if (confirmPassword.isEmpty()) {
                tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
                isValid = false;
            } else if (!confirmPassword.equals(newPassword)) {
                tilConfirmPassword.setError("Mật khẩu không khớp");
                isValid = false;
            }

            // Kiểm tra mật khẩu mới không giống mật khẩu cũ
            if (!newPassword.isEmpty() && newPassword.equals(currentPassword)) {
                tilNewPassword.setError("Mật khẩu mới phải khác mật khẩu hiện tại");
                isValid = false;
            }
        }

        return isValid;
    }

    private void saveProfile() {
        // Lấy dữ liệu từ form
        String fullName = etFullName.getText().toString().trim();
        String genderText = actvGender.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim(); // dd/MM/yyyy
        String idCard = etIdCard.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Map gender to API expected string: "1" = Nam, "0" = Nữ
        String genderPayload = "";
        if ("Nam".equalsIgnoreCase(genderText))
            genderPayload = "1";
        else if ("Nữ".equalsIgnoreCase(genderText) || "Nu".equalsIgnoreCase(genderText))
            genderPayload = "0";

        // Get user id from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("key_user_id", 1);

        UpdateClientProfileRequest request = new UpdateClientProfileRequest(
                fullName,
                email,
                idCard,
                genderPayload,
                birthDate,
                address,
                phone
        );

        ReaderApiService service = ApiClient.getClient().create(ReaderApiService.class);
        Call<ResponseSingleModel<ReaderProfileResponse>> call = service.updateProfile(userId, request);
        call.enqueue(new Callback<ResponseSingleModel<ReaderProfileResponse>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<ReaderProfileResponse>> call, Response<ResponseSingleModel<ReaderProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ReaderProfileResponse updated = response.body().getData();

                    String newName = (updated != null && updated.getHoTenDG() != null && !updated.getHoTenDG().isEmpty()) ? updated.getHoTenDG() : fullName;
                    String newEmail = (updated != null && updated.getEmailDG() != null && !updated.getEmailDG().isEmpty()) ? updated.getEmailDG() : email;
                    prefs.edit()
                            .putString("key_username", newName)
                            .putString("key_userEmail", newEmail)
                            .apply();

                    String successMsg = response.body().getMessage() != null && !response.body().getMessage().isEmpty()
                            ? response.body().getMessage() : "Cập nhật thông tin thành công";
                    Toasty.success(requireContext(), successMsg, Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    String errMsg = "Cập nhật thất bại";
                    if (response.body() != null && response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                        errMsg = response.body().getMessage();
                    } else if (!response.isSuccessful()) {
                        errMsg = "Server trả lỗi mã " + response.code();
                    }
                    Toasty.error(requireContext(), errMsg, Toast.LENGTH_SHORT).show();
                }
             }

             @Override
             public void onFailure(Call<ResponseSingleModel<ReaderProfileResponse>> call, Throwable t) {
                 Log.w("EditProfile", "updateProfile failed: " + t.getMessage());
                 Toasty.error(requireContext(), "Lỗi mạng khi cập nhật: " + t.getMessage(), Toast.LENGTH_LONG).show();
             }
         });
     }

    private void loadUserData() {
        int userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getInt("key_user_id", 2);

        ReaderApiService service = ApiClient.getClient().create(ReaderApiService.class);
        Call<ResponseSingleModel<ReaderProfileResponse>> call = service.getProfileInfo(userId);
        call.enqueue(new Callback<ResponseSingleModel<ReaderProfileResponse>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<ReaderProfileResponse>> call, Response<ResponseSingleModel<ReaderProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ReaderProfileResponse profile = response.body().getData();

                    etFullName.setText(profile.getHoTenDG());
                    // gender
                    if (profile.isGioiTinh()) actvGender.setText("Nam", false);
                    else actvGender.setText("Nữ", false);

                    // parse ISO date (if returned) to dd/MM/yyyy
                    String isoDate = profile.getNgaySinh();
                    String displayDate = "";
                    if (isoDate != null && !isoDate.isEmpty()) {
                        try {
                            java.text.SimpleDateFormat input = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                            java.util.Date d = input.parse(isoDate);
                            if (d != null) displayDate = dateFormatter.format(d);
                        } catch (Exception e) {
                            Log.w("EditProfile", "Failed to parse date: " + isoDate + " -> " + e.getMessage());
                            displayDate = isoDate;
                        }
                    }
                    etBirthDate.setText(displayDate);

                    etIdCard.setText(profile.getSoCMND());
                    etEmail.setText(profile.getEmailDG());
                    etPhone.setText(profile.getDienThoai());
                    etAddress.setText(profile.getDiaChiDG());
                } else {
                    Toasty.error(requireContext(), response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<ReaderProfileResponse>> call, Throwable t) {
                String err = t != null && t.getMessage() != null ? t.getMessage() : "Lỗi mạng";
                Toasty.error(requireContext(), "Không thể tải profile: " + err, Toasty.LENGTH_LONG).show();
                Log.w("EditProfile", "loadUserData failed: " + err);
                requireActivity().onBackPressed();
            }
        });
    }

    private void clearPasswordFields() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
        tilCurrentPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);
    }
}
