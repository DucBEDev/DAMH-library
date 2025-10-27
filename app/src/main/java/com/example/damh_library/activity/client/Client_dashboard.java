package com.example.damh_library.activity.client;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.damh_library.R;
import com.example.damh_library.fragment.client.AccountFragment;
import com.example.damh_library.fragment.client.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Client_dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_dashboard);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentClientDashboard, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Mặc định hiển thị trang home
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
}