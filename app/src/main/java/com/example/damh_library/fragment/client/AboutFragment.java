package com.example.damh_library.fragment.client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.damh_library.R;

public class AboutFragment extends Fragment {

    private ImageButton btnBack, btnFacebook, btnWebsite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnFacebook = view.findViewById(R.id.btnFacebook);
        btnWebsite = view.findViewById(R.id.btnWebsite);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnFacebook.setOnClickListener(v -> {
            openUrl("https://facebook.com/thuviensach");
        });

        btnWebsite.setOnClickListener(v -> {
            openUrl("https://thuvien.example.com");
        });
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Không thể mở liên kết", Toast.LENGTH_SHORT).show();
        }
    }
}