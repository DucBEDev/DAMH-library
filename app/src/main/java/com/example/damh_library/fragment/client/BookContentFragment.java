package com.example.damh_library.fragment.client;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.damh_library.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookContentFragment extends Fragment {

    private static final String ARG_PDF_URL = "pdf_url";
    private static final String ARG_BOOK_TITLE = "book_title";

    private WebView webView;
    private ProgressBar progressBar;


    public static BookContentFragment newInstance(String pdfUrl) {
        BookContentFragment fragment = new BookContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_URL, pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_content, container, false);

        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);


        String pdfUrl = getArguments() != null ? getArguments().getString(ARG_PDF_URL) : null;

        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            loadPdfWithGoogleDocs(pdfUrl);
        } else {
            webView.loadData("<h3 style='text-align:center;margin-top:50px'>Không tìm thấy file PDF</h3>", "text/html", "UTF-8");
        }

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadPdfWithGoogleDocs(String url) {
        progressBar.setVisibility(View.VISIBLE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Google Drive preview
        if (url.contains("drive.google.com") && url.contains("/file/d/")) {
            String fileId = url.split("/d/")[1].split("/")[0];
            url = "https://drive.google.com/file/d/" + fileId + "/preview";
        }

        // Dropbox remove landing page
        if (url.contains("dropbox.com")) {
            url = url.replace("dl=0", "raw=1");
        }

        // Firebase: no change needed

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.loadUrl(url);
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