package com.example.damh_library.fragment.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.damh_library.R;
import com.example.damh_library.adapter.BookViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewBookFragment extends Fragment {

    private static final String ARG_PDF_URL = "pdf_url";
    private static final String ARG_BOOK_TITLE = "book_title";
    private static final String ARG_PDF_FILENAME = "pdf_filename";

    private TextView tvBookTitle;
    private ImageButton btnBack;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BookViewPagerAdapter pagerAdapter;

    public static ViewBookFragment newInstance(String pdfUrl, String bookTitle) {
        ViewBookFragment fragment = new ViewBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_URL, pdfUrl);
        args.putString(ARG_BOOK_TITLE, bookTitle);

        // Extract filename from URL or use default
        String filename = extractFilenameFromUrl(pdfUrl);
        args.putString(ARG_PDF_FILENAME, filename);

        fragment.setArguments(args);
        return fragment;
    }

    private static String extractFilenameFromUrl(String url) {
        // For OneDrive links, use a default filename
        if (url != null && url.contains("1drv.ms")) {
            return "document.pdf";
        }

        // Extract filename from URL
        if (url != null && url.contains("/")) {
            String[] parts = url.split("/");
            return parts[parts.length - 1];
        }

        return "document.pdf";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_book, container, false);

        initViews(view);
        setupViewPager();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        btnBack = view.findViewById(R.id.btnBack);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set book title from arguments
        if (getArguments() != null) {
            String bookTitle = getArguments().getString(ARG_BOOK_TITLE, "Đọc sách");
            tvBookTitle.setText(bookTitle);
        }
    }

    private void setupViewPager() {
        String pdfUrl = getArguments() != null ? getArguments().getString(ARG_PDF_URL) : "";
        String pdfFilename = getArguments() != null ? getArguments().getString(ARG_PDF_FILENAME) : "";

        pagerAdapter = new BookViewPagerAdapter(
                requireActivity(),
                pdfUrl,
                pdfFilename
        );

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1); // Keep both fragments in memory

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Nội dung");
                    tab.setIcon(R.drawable.ic_book);
                    break;
                case 1:
                    tab.setText("Trợ lý AI");
                    tab.setIcon(R.drawable.ic_book);
                    break;
            }
        }).attach();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up ViewPager
        if (viewPager != null) {
            viewPager.setAdapter(null);
        }
    }
}