package com.example.damh_library.fragment.client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.model.response.ReaderProfileResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.ReaderApiService;
import com.example.damh_library.model.ResponseSingleModel;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUserName, tvEmail;
    private TextView tvFullName, tvGender, tvBirthDate, tvIdCard;
    private TextView tvEmailDetail, tvPhone, tvAddress;
    private MaterialButton btnEditProfile;

    private ImageButton btnBack;

    private ReaderProfileResponse readerProfileResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_profile, container, false);

        initViews(view);
        loadUserProfile();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);

        tvFullName = view.findViewById(R.id.tvFullName);
        tvGender = view.findViewById(R.id.tvGender);
        tvBirthDate = view.findViewById(R.id.tvBirthDate);
        tvIdCard = view.findViewById(R.id.tvIdCard);

        tvEmailDetail = view.findViewById(R.id.tvEmailDetail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void loadUserProfile() {
        int userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getInt("key_user_id", 2);

        ReaderApiService service = ApiClient.getClient().create(ReaderApiService.class);
        Call<ResponseSingleModel<ReaderProfileResponse>> call = service.getProfileInfo(userId);
        call.enqueue(new Callback<ResponseSingleModel<ReaderProfileResponse>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<ReaderProfileResponse>> call, Response<ResponseSingleModel<ReaderProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    readerProfileResponse = response.body().getData();
                    displayUserProfile(readerProfileResponse);
                } else {
                    loadSampleProfile();
                    Toasty.error(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<ReaderProfileResponse>> call, Throwable t) {
                loadSampleProfile();
                Toast.makeText(getContext(), "Lỗi mạng khi tải profile: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadSampleProfile() {
        readerProfileResponse = new ReaderProfileResponse(
                "NULL",
                "NULL",
                "NULL",
                false,
                "1998-12-15T00:00:00.000Z",
                "NULL",
                "NULL"
        );
        displayUserProfile(readerProfileResponse);
    }

    private void displayUserProfile(ReaderProfileResponse profile) {
        // Header
        tvUserName.setText(profile.getHoTenDG());
        tvEmail.setText(profile.getEmailDG());

        // Thông tin cá nhân
        tvFullName.setText(profile.getHoTenDG());
        tvGender.setText(profile.isGioiTinh() ? "Nam" : "Nữ");
        tvBirthDate.setText(formatDate(profile.getNgaySinh()));
        tvIdCard.setText(profile.getSoCMND());

        // Thông tin liên hệ
        tvEmailDetail.setText(profile.getEmailDG());
        tvPhone.setText(profile.getDienThoai());
        tvAddress.setText(profile.getDiaChiDG());

        // Load avatar nếu có URL (hiện tại chưa có nhưng giữ chỗ)
        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(profile.getAvatarUrl())
                    .fitCenter()
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_user);
        }
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            if (date == null) return dateString;
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.w("ClientProfile", "Failed to parse date: " + dateString + " -> " + e.getMessage());
            return dateString;
        }
    }

    private void setupListeners() {
        // Nút chỉnh sửa thông tin
        btnEditProfile.setOnClickListener(v -> {
            EditClientProfileFragment editClientProfileFragment = new EditClientProfileFragment();

            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentClientDashboard, editClientProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
}