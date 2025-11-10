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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.damh_library.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import com.example.damh_library.activity.MainActivity;

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
            FavoriteBookFragment favoriteCategoryFragment = new FavoriteBookFragment();

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
        // Load user data from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("key_username", "");

        if (username != null && !username.isEmpty()) {
            tvUserName.setText(username);
        } else {
            tvUserName.setText(getString(R.string.default_username));
        }

        // If you have user email stored, read it; otherwise keep existing placeholder or empty
        String email = prefs.getString("key_userEmail", "");
        if (email != null && !email.isEmpty()) {
            tvUserEmail.setText(email);
        }

        // TODO: Load profile image using Glide or Picasso if available
        // Glide.with(this).load(userImageUrl).into(imgProfile);
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