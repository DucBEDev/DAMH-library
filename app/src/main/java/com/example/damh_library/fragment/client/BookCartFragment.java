package com.example.damh_library.fragment.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.damh_library.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class BookCartFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_cart, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        setupViewPager();

        return view;
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(SubCartFragment.newInstance(SubCartFragment.TYPE_PHYSICAL), "Sách vật lý");
        adapter.addFragment(SubCartFragment.newInstance(SubCartFragment.TYPE_ONLINE), "Sách trực tuyến");

        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();
    }

    static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> titles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}