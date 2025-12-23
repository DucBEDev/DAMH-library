package com.example.damh_library.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.damh_library.R;

public class PdfContentFragment extends Fragment {

    private static final String TAG = "PdfContentFragment";
    private static final String ARG_PDF_URL = "pdf_url";

    private WebView webView;
    private ProgressBar progressBar;
    private String pdfUrl;

    public static PdfContentFragment newInstance(String pdfUrl) {
        PdfContentFragment fragment = new PdfContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_URL, pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pdfUrl = getArguments().getString(ARG_PDF_URL, "");
            Log.d(TAG, "PDF URL: " + pdfUrl);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_content, container, false);

        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);

        setupWebView();
        loadPdf();

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        if (webView == null) {
            Log.e(TAG, "WebView is null");
            return;
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Important settings for PDF viewing
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Log.d(TAG, "Page loaded successfully");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "WebView error: " + description);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Lỗi tải PDF: " + description,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPdf() {
        if (pdfUrl == null || pdfUrl.isEmpty()) {
            Log.e(TAG, "PDF URL is empty");
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (getContext() != null) {
                Toast.makeText(getContext(),
                        "Không có link PDF để tải",
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (webView == null) {
            Log.e(TAG, "WebView is null");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String finalUrl = convertToViewableUrl(pdfUrl);

        Log.d(TAG, "Original URL: " + pdfUrl);
        Log.d(TAG, "Loading URL: " + finalUrl);

        webView.loadUrl(finalUrl);
    }

    /**
     * Convert various PDF URL formats to viewable format
     */
    private String convertToViewableUrl(String url) {
        // Handle OneDrive links
        if (url.contains("1drv.ms") || url.contains("onedrive.live.com")) {
            // OneDrive direct view format
            // Change from: https://1drv.ms/b/c/xxx/xxx
            // To: https://onedrive.live.com/embed?resid=xxx&authkey=xxx

            // For now, try direct OneDrive embed
            if (url.contains("1drv.ms")) {
                // OneDrive short links need to be opened directly
                return url;
            } else if (url.contains("onedrive.live.com")) {
                // Add embed parameter if not present
                if (!url.contains("embed")) {
                    return url.replace("view.aspx", "embed");
                }
            }
        }

        // Handle direct PDF URLs - use Google Docs Viewer
        if (url.endsWith(".pdf") || url.contains(".pdf?")) {
            return "https://docs.google.com/gview?embedded=true&url=" + url;
        }

        // Handle Google Drive links
        if (url.contains("drive.google.com")) {
            // Extract file ID and create viewer URL
            String fileId = extractGoogleDriveFileId(url);
            if (fileId != null) {
                return "https://drive.google.com/file/d/" + fileId + "/preview";
            }
        }

        // Default: return original URL
        return url;
    }

    /**
     * Extract file ID from Google Drive URL
     */
    private String extractGoogleDriveFileId(String url) {
        if (url.contains("/d/")) {
            int startIndex = url.indexOf("/d/") + 3;
            int endIndex = url.indexOf("/", startIndex);
            if (endIndex > startIndex) {
                return url.substring(startIndex, endIndex);
            } else {
                return url.substring(startIndex);
            }
        } else if (url.contains("id=")) {
            int startIndex = url.indexOf("id=") + 3;
            int endIndex = url.indexOf("&", startIndex);
            if (endIndex > startIndex) {
                return url.substring(startIndex, endIndex);
            } else {
                return url.substring(startIndex);
            }
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
    }
}