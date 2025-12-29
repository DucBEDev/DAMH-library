package com.example.damh_library.fragment.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.activity.MainActivity;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.response.ReaderProfileResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.ReaderApiService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private ShapeableImageView imgProfile;
    private TextView tvUserName, tvUserEmail;
    private Button btnEditProfile, btnLogout;
    private LinearLayout layoutPersonalInfo, layoutMyReaderCard, layoutFavCategory,
            layoutPolicy, layoutAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);
        setupListeners();
        loadUserData();

        return view;
    }

    private void initViews(View view) {
        // Profile section
        imgProfile = view.findViewById(R.id.imgProfile);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Account options
        layoutPersonalInfo = view.findViewById(R.id.layoutPersonalInfo);
        layoutMyReaderCard = view.findViewById(R.id.layoutMyReaderCard);
        layoutFavCategory = view.findViewById(R.id.layoutFavCategory);

        // Settings
        layoutPolicy = view.findViewById(R.id.layoutPolicy);
        layoutAbout = view.findViewById(R.id.layoutAbout);

        // Logout button
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload user data when fragment resumes to get updated avatar
        loadUserDataFromApi();
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            EditClientProfileFragment editClientProfileFragment = new EditClientProfileFragment();

            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentClientDashboard, editClientProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        layoutPersonalInfo.setOnClickListener(v -> {
            ClientProfileFragment clientProfileFragment = new ClientProfileFragment();

            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentClientDashboard, clientProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        layoutMyReaderCard.setOnClickListener(v -> {
            ReaderCardFragment readerCardFragment = new ReaderCardFragment();

            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentClientDashboard, readerCardFragment)
                    .addToBackStack(null)
                    .commit();
        });

        layoutFavCategory.setOnClickListener(v -> {
            BookCartFragment favoriteCategoryFragment = new BookCartFragment();

            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentClientDashboard, favoriteCategoryFragment)
                    .addToBackStack(null)
                    .commit();
        });

        layoutPolicy.setOnClickListener(v -> {
            PolicyFragment policyFragment = new PolicyFragment();

            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentClientDashboard, policyFragment)
                    .addToBackStack(null)
                    .commit();
        });

        layoutAbout.setOnClickListener(v -> {
            AboutFragment aboutFragment = new AboutFragment();

            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentClientDashboard, aboutFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserData() {
        // Load từ API thay vì chỉ SharedPreferences
        loadUserDataFromApi();
    }

    private void loadUserDataFromApi() {
        // Load basic data từ SharedPreferences trước
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("key_username", "");
        String email = prefs.getString("key_userEmail", "");
        String userId = prefs.getString("key_userId", "2");

        // Hiển thị data cơ bản trước
        if (username != null && !username.isEmpty()) {
            tvUserName.setText(username);
        } else {
            tvUserName.setText(getString(R.string.default_username));
        }

        if (email != null && !email.isEmpty()) {
            tvUserEmail.setText(email);
        }

        // Gọi API để lấy avatar và thông tin mới nhất
        ReaderApiService service = ApiClient.getClient().create(ReaderApiService.class);
        Call<ResponseSingleModel<ReaderProfileResponse>> call = service.getProfileInfo(userId);

        call.enqueue(new Callback<ResponseSingleModel<ReaderProfileResponse>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<ReaderProfileResponse>> call, Response<ResponseSingleModel<ReaderProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ReaderProfileResponse profile = response.body().getData();

                    // Cập nhật thông tin mới nhất
                    String fullName = profile.getHoTenDG();
                    String emailProfile = profile.getEmailDG();
                    String avatarUrl = profile.getAvatar();

                    if (fullName != null && !fullName.isEmpty()) {
                        tvUserName.setText(fullName);
                        // Cập nhật lại SharedPreferences
                        prefs.edit().putString("key_username", fullName).apply();
                    }

                    if (emailProfile != null && !emailProfile.isEmpty()) {
                        tvUserEmail.setText(emailProfile);
                        // Cập nhật lại SharedPreferences
                        prefs.edit().putString("key_userEmail", emailProfile).apply();
                    }

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(AccountFragment.this)
                                .load(avatarUrl)
                                .centerCrop()
                                .placeholder(R.drawable.ic_account)
                                .error(R.drawable.ic_account)
                                .into(imgProfile);
                    } else {
                        imgProfile.setImageResource(R.drawable.ic_account);
                    }
                } else {
                    imgProfile.setImageResource(R.drawable.ic_account);
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<ReaderProfileResponse>> call, Throwable t) {
                imgProfile.setImageResource(R.drawable.ic_account);
            }
        });
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void performLogout() {
        // Clear SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Navigate back to login screen (MainActivity)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}