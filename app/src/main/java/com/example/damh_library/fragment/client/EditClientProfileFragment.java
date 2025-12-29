package com.example.damh_library.fragment.client;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.UpdateClientProfileRequest;
import com.example.damh_library.model.response.ReaderProfileResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.ReaderApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditClientProfileFragment extends Fragment {

    // UI Components
    private FloatingActionButton fabChangeAvatar; // Giữ nguyên FloatingActionButton
    private TextInputEditText etFullName, etBirthDate, etIdCard;
    private TextInputEditText etEmail, etPhone, etAddress;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private AutoCompleteTextView actvGender;
    private SwitchMaterial switchChangePassword;
    private LinearLayout llPasswordFields;
    private MaterialButton btnCancel, btnSave;
    private ImageButton btnBack;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private ImageView ivCurrentAvatar;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private Uri selectedImageUri;
    private String currentAvatarUrl;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_edit_client_profile, container, false);

            initViews(view);
            setupImagePickers();
            setupGenderDropdown();
            setupDatePicker();
            setupPasswordSwitch();
            setupValidation();
            setupButtons();
            loadUserData();

            return view;
        } catch (Exception e) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return null;
        }
    }

    private void initViews(View view) {
        try {
            fabChangeAvatar = view.findViewById(R.id.fabChangeAvatar);
            ivCurrentAvatar = view.findViewById(R.id.ivCurrentAvatar);

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
            btnBack = view.findViewById(R.id.btnBack);
        } catch (Exception e) {
            throw e;
        }
    }

    private void setupImagePickers() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    displaySelectedImage();
                }
            }
        );

        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = (Uri) result.getData().getExtras().get("data");
                    displaySelectedImage();
                }
            }
        );
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
    }

    private void setupButtons() {
        try {
            if (btnCancel != null) {
                btnCancel.setOnClickListener(v -> {
                    requireActivity().onBackPressed();
                });
            }

            if (btnSave != null) {
                btnSave.setOnClickListener(v -> {
                    if (validateInputs()) {
                        saveProfile();
                    }
                });
            }

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    requireActivity().onBackPressed();
                });
            }

            if (fabChangeAvatar != null) {
                fabChangeAvatar.setOnClickListener(v -> {
                    showImagePickerDialog();
                });
            }
        } catch (Exception e) {
        }
    }

    private void showImagePickerDialog() {
        if (getContext() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn ảnh đại diện")
                .setItems(new String[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(requireContext(), "Camera không khả dụng", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        imagePickerLauncher.launch(galleryIntent);
    }

    private void displaySelectedImage() {
        if (selectedImageUri != null && ivCurrentAvatar != null) {
            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(ivCurrentAvatar);
        }
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
        if (!validateInputs()) {
            return;
        }
        updateProfile();
    }

    private void updateProfile() {
        try {
            // Lấy dữ liệu từ form
            String fullName = etFullName.getText().toString().trim();
            String genderText = actvGender.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();
            String idCard = etIdCard.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Map gender
            String genderPayload = "Nam".equalsIgnoreCase(genderText) ? "1" : "0";

            SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String userId = prefs.getString("key_userId", "1");

            // Tạo RequestBody cho các field text
            RequestBody hoTenDGBody = RequestBody.create(MediaType.parse("text/plain"), fullName);
            RequestBody emailDGBody = RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody soCMNDBody = RequestBody.create(MediaType.parse("text/plain"), idCard);
            RequestBody gioiTinhBody = RequestBody.create(MediaType.parse("text/plain"), genderPayload);
            RequestBody ngaySinhBody = RequestBody.create(MediaType.parse("text/plain"), birthDate);
            RequestBody diaChiDGBody = RequestBody.create(MediaType.parse("text/plain"), address);
            RequestBody dienThoaiBody = RequestBody.create(MediaType.parse("text/plain"), phone);

            // Tạo avatar part (có thể null nếu không chọn ảnh mới)
            MultipartBody.Part avatarPart = null;
            if (selectedImageUri != null) {
                File imageFile = createFileFromUri(selectedImageUri);
                if (imageFile != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                    avatarPart = MultipartBody.Part.createFormData("avatar", imageFile.getName(), requestFile);
                }
            }

            // Tạo RequestBody cho hasNewImage: true nếu người dùng chọn ảnh mới
            boolean hasNewImageFlag = (selectedImageUri != null);
            RequestBody hasNewImageBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(hasNewImageFlag));

            // Nếu không có ảnh mới, gửi currentImagePath (URL hiện tại) cho backend
            String currentImagePath = "";
            if (!hasNewImageFlag && currentAvatarUrl != null) {
                currentImagePath = currentAvatarUrl;
            }
            RequestBody currentImagePathBody = RequestBody.create(MediaType.parse("text/plain"), currentImagePath);

             // Gọi API với @Path userId thay vì @Query
            ReaderApiService service = ApiClient.getClient().create(ReaderApiService.class);
            Call<ResponseSingleModel<ReaderProfileResponse>> call = service.updateProfile(
                userId, hoTenDGBody, emailDGBody, soCMNDBody, gioiTinhBody, 
                ngaySinhBody, diaChiDGBody, dienThoaiBody, currentImagePathBody, hasNewImageBody, avatarPart
            );
            
            call.enqueue(new Callback<ResponseSingleModel<ReaderProfileResponse>>() {
                @Override
                public void onResponse(Call<ResponseSingleModel<ReaderProfileResponse>> call, 
                                     Response<ResponseSingleModel<ReaderProfileResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            ReaderProfileResponse updated = response.body().getData();

                            // Cập nhật SharedPreferences với thông tin mới
                            String newName = (updated != null && updated.getHoTenDG() != null) ? 
                                           updated.getHoTenDG() : fullName;
                            String newEmail = (updated != null && updated.getEmailDG() != null) ? 
                                            updated.getEmailDG() : email;
                            
                            prefs.edit()
                                    .putString("key_username", newName)
                                    .putString("key_userEmail", newEmail)
                                    .apply();

                            String successMsg = (response.body().getMessage() != null) ? 
                                              response.body().getMessage() : "Cập nhật thành công";
                            Toasty.success(requireContext(), successMsg, Toast.LENGTH_SHORT).show();
                            
                            requireActivity().onBackPressed();
                        } else {
                            String errMsg = (response.body().getMessage() != null) ?
                                          response.body().getMessage() : "Cập nhật thất bại";
                            Toasty.error(requireContext(), errMsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(requireContext(), "Cập nhật thất bại (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseSingleModel<ReaderProfileResponse>> call, Throwable t) {
                    Toasty.error(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    private File createFileFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = new File(requireContext().getCacheDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            return null;
        }
    }

    private void loadUserData() {
        try {
            String userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("key_userId", "2");

            ReaderApiService service = ApiClient.getClient().create(ReaderApiService.class);
            Call<ResponseSingleModel<ReaderProfileResponse>> call = service.getProfileInfo(userId);
            call.enqueue(new Callback<ResponseSingleModel<ReaderProfileResponse>>() {
                @Override
                public void onResponse(Call<ResponseSingleModel<ReaderProfileResponse>> call, Response<ResponseSingleModel<ReaderProfileResponse>> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            ReaderProfileResponse profile = response.body().getData();

                            currentAvatarUrl = profile.getAvatar();
                            if (currentAvatarUrl != null && !currentAvatarUrl.isEmpty() && ivCurrentAvatar != null) {
                                Glide.with(EditClientProfileFragment.this)
                                        .load(currentAvatarUrl)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_user)
                                        .error(R.drawable.ic_user)
                                        .into(ivCurrentAvatar);
                            } else if (ivCurrentAvatar != null) {
                                ivCurrentAvatar.setImageResource(R.drawable.ic_user);
                            }
                            if (etFullName != null) etFullName.setText(profile.getHoTenDG());
                            if (actvGender != null) {
                                if (profile.isGioiTinh()) actvGender.setText("Nam", false);
                                else actvGender.setText("Nữ", false);
                            }

                            // Parse date
                            String isoDate = profile.getNgaySinh();
                            String displayDate = "";
                            if (isoDate != null && !isoDate.isEmpty()) {
                                try {
                                    java.text.SimpleDateFormat input = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                                    java.util.Date d = input.parse(isoDate);
                                    if (d != null) displayDate = dateFormatter.format(d);
                                } catch (Exception e) {
                                    displayDate = isoDate;
                                }
                            }
                            if (etBirthDate != null) etBirthDate.setText(displayDate);

                            if (etIdCard != null) etIdCard.setText(profile.getSoCMND());
                            if (etEmail != null) etEmail.setText(profile.getEmailDG());
                            if (etPhone != null) etPhone.setText(profile.getDienThoai());
                            if (etAddress != null) etAddress.setText(profile.getDiaChiDG());
                        } else {
                            Toasty.error(requireContext(), "Không thể tải thông tin profile", Toasty.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        }
                    } catch (Exception e) {
                        Toasty.error(requireContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                }

                @Override
                public void onFailure(Call<ResponseSingleModel<ReaderProfileResponse>> call, Throwable t) {
                    String err = t != null && t.getMessage() != null ? t.getMessage() : "Lỗi mạng";
                    Toasty.error(requireContext(), "Không thể tải profile: " + err, Toasty.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                }
            });
        } catch (Exception e) {
            Toasty.error(requireContext(), "Lỗi khởi tạo: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
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
