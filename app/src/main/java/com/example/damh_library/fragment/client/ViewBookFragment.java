package com.example.damh_library.fragment.client;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.damh_library.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ViewBookFragment extends Fragment {

    private static final String ARG_PDF_URL = "pdf_url";
    private static final String ARG_BOOK_TITLE = "book_title";

    private ImageButton btnBack;
    private TextView tvBookTitle;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;


    public static ViewBookFragment newInstance(String pdfUrl, String bookTitle) {
        ViewBookFragment fragment = new ViewBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_URL, pdfUrl);
        args.putString(ARG_BOOK_TITLE, bookTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_book, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        btnBack = view.findViewById(R.id.btnBack);

        String pdfUrl = getArguments() != null ? getArguments().getString(ARG_PDF_URL) : null;
        String bookTitle = getArguments() != null ? getArguments().getString(ARG_BOOK_TITLE, "Đọc sách") : "Đọc sách";

        tvBookTitle.setText(bookTitle);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        setupViewPager();

        return view;
    }

    private void setupViewPager() {
        String pdf = "https://drive.google.com/file/d/1Tj-Jo1OEdWGp9f4GBKiZtUWRc4T80RGo/view";
        ViewBookFragment.ViewPagerAdapter adapter = new ViewBookFragment.ViewPagerAdapter(this);
        adapter.addFragment(BookContentFragment.newInstance(pdf), "Nội dung sách");
        adapter.addFragment(PdfReaderFragment.newInstance(), "Trợ lý đọc sách");

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