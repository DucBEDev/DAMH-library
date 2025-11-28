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

import com.example.damh_library.R;

public class ViewBookFragment extends Fragment {

    private static final String ARG_PDF_URL = "pdf_url";
    private static final String ARG_BOOK_TITLE = "book_title";

    private WebView webView;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private TextView tvBookTitle;

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

        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);
        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        btnBack = view.findViewById(R.id.btnBack);

        String pdfUrl = getArguments() != null ? getArguments().getString(ARG_PDF_URL) : null;
        String bookTitle = getArguments() != null ? getArguments().getString(ARG_BOOK_TITLE, "Đọc sách") : "Đọc sách";

        tvBookTitle.setText(bookTitle);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            loadPdfWithGoogleDocs(pdfUrl);
        } else {
            webView.loadData("<h3 style='text-align:center;margin-top:50px'>Không tìm thấy file PDF</h3>", "text/html", "UTF-8");
        }

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadPdfWithGoogleDocs(String pdfUrl) {
        progressBar.setVisibility(View.VISIBLE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        // CÁCH TỐT NHẤT: Dùng chính xác link embed bạn có
        String finalUrl = pdfUrl;  // đã là link embed rồi

        // Hoặc nếu bạn có link share ngắn (1drv.ms), thêm ?embed=1
        if (pdfUrl.contains("1drv.ms")) {
            finalUrl = pdfUrl + "?embed=1";
        }

        webView.loadUrl(finalUrl);
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroyView();
    }
}