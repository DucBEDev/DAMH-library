package com.example.damh_library.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.damh_library.fragment.PdfContentFragment;
import com.example.damh_library.fragment.PdfReaderFragment;

public class BookViewPagerAdapter extends FragmentStateAdapter {
    
    private static final String TAG = "BookViewPagerAdapter";
    
    private String pdfUrl;
    private String pdfFilename;
    
    public BookViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, 
                                String pdfUrl, 
                                String pdfFilename) {
        super(fragmentActivity);
        this.pdfUrl = pdfUrl;
        this.pdfFilename = pdfFilename;
        
        Log.d(TAG, "Adapter created with URL: " + pdfUrl + ", Filename: " + pdfFilename);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            Log.d(TAG, "Creating fragment at position: " + position);
            switch (position) {
                case 0:
                    return PdfContentFragment.newInstance(pdfUrl != null ? pdfUrl : "");
                case 1:
                    return PdfReaderFragment.newInstance(pdfFilename != null ? pdfFilename : "");
                default:
                    return PdfContentFragment.newInstance(pdfUrl != null ? pdfUrl : "");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating fragment: " + e.getMessage(), e);
            return new Fragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 2;
    }
}
