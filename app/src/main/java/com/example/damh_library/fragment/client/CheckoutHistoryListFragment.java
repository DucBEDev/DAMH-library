package com.example.damh_library.fragment.client;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.CheckoutHistoryAdapter;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.response.BookInCheckoutResponse;
import com.example.damh_library.model.response.CheckoutHistoryResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.CheckoutSlipApiService;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutHistoryListFragment extends Fragment {

    private RecyclerView rvCheckoutHistory;
    private CheckoutHistoryAdapter adapter;
    private TextView tvTotalRecords;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private EditText etSearch;
    private ImageView btnClearSearch;

    private Runnable searchRunnable;

    public CheckoutHistoryListFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout_history_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setupRecyclerView();
        setupSearchFunction();
        loadCheckoutHistory();
    }

    private void initViews(View view) {
        rvCheckoutHistory = view.findViewById(R.id.rvCheckoutHistory);
        tvTotalRecords = view.findViewById(R.id.tvTotalRecords);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        progressBar = view.findViewById(R.id.progressBar);
        etSearch = view.findViewById(R.id.etSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
    }

    private void setupRecyclerView() {
        rvCheckoutHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CheckoutHistoryAdapter(new ArrayList<>());
        
        adapter.setOnBookClickListener(book -> {
            openBookDetail(book);
        });
        
        rvCheckoutHistory.setAdapter(adapter);
    }

    private void setupSearchFunction() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hiển thị/ẩn nút clear
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().trim();
                
                // Debounce search để tránh gọi API liên tục
                if (searchRunnable != null) {
                    etSearch.removeCallbacks(searchRunnable);
                }
                
                searchRunnable = () -> {
                    if (searchQuery.isEmpty()) {
                        loadCheckoutHistory(); // Load tất cả nếu search rỗng
                    } else {
                        searchCheckoutHistory(searchQuery); // Tìm kiếm theo mã phiếu
                    }
                };
                
                etSearch.postDelayed(searchRunnable, 500); // Delay 500ms
            }
        });

        // Setup clear search button
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
            loadCheckoutHistory(); // Load lại tất cả dữ liệu
        });
    }

    private void loadCheckoutHistory() {
        showLoading(true);
        
        String userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("key_userId", "5");

        CheckoutSlipApiService service = ApiClient.getClient().create(CheckoutSlipApiService.class);
        Call<ResponseModel<CheckoutHistoryResponse>> call = service.getReaderCheckoutHistory(userId);
        
        handleApiResponse(call, "Tải danh sách phiếu mượn");
    }

    private void searchCheckoutHistory(String maPhieu) {
        showLoading(true);
        
        String userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("key_userId", "5");

        CheckoutSlipApiService service = ApiClient.getClient().create(CheckoutSlipApiService.class);

        Call<ResponseModel<CheckoutHistoryResponse>> call = service.searchCheckoutHistory(userId, maPhieu);
        
        handleApiResponse(call, "Tìm kiếm phiếu mượn");
    }

    private void handleApiResponse(Call<ResponseModel<CheckoutHistoryResponse>> call, String operation) {
        call.enqueue(new Callback<ResponseModel<CheckoutHistoryResponse>>() {
            @Override
            public void onResponse(Call<ResponseModel<CheckoutHistoryResponse>> call, 
                                 Response<ResponseModel<CheckoutHistoryResponse>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel<CheckoutHistoryResponse> body = response.body();
                    
                    if (body.isSuccess() && body.getData() != null && !body.getData().isEmpty()) {
                        List<CheckoutHistoryResponse> checkouts = body.getData();
                        
                        adapter.setItems(checkouts);
                        updateUI(checkouts.size(), false);
                    } else {
                        // Không có dữ liệu
                        adapter.setItems(new ArrayList<>());
                        updateUI(0, true);
                        
                        String message = body.getMessage() != null ? body.getMessage() : 
                                        (operation.contains("Tìm kiếm") ? "Không tìm thấy phiếu mượn" : "Chưa có lịch sử mượn sách");
                        Toasty.info(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    adapter.setItems(new ArrayList<>());
                    updateUI(0, true);
                    Toasty.error(requireContext(), operation + " thất bại (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<CheckoutHistoryResponse>> call, Throwable t) {
                showLoading(false);
                adapter.setItems(new ArrayList<>());
                updateUI(0, true);
                
                Toasty.error(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            rvCheckoutHistory.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateUI(int totalCount, boolean isEmpty) {
        // Cập nhật số lượng tổng
        tvTotalRecords.setText("Tổng số: " + totalCount + " phiếu");
        
        if (isEmpty) {
            rvCheckoutHistory.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvCheckoutHistory.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void openBookDetail(BookInCheckoutResponse book) {
        try {
            // Trích xuất ISBN từ mã sách (format: ISBN-XX)
            String bookCode = book.getMaSach();
            String isbn = extractISBNFromBookCode(bookCode);
            
            if (isbn != null && !isbn.isEmpty()) {
                BookDetailFragment bookDetailFragment = BookDetailFragment.newInstance(isbn);
                
                // Chuyển qua BookDetailFragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentClientDashboard, bookDetailFragment)
                        .addToBackStack(null)
                        .commit();
                        
                Toasty.info(requireContext(), "Đang tải chi tiết sách: " + book.getTenSach(), Toast.LENGTH_SHORT).show();
            } else {
                Toasty.error(requireContext(), "Không thể xác định thông tin sách", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toasty.error(requireContext(), "Lỗi khi mở chi tiết sách", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractISBNFromBookCode(String bookCode) {
        if (bookCode == null || bookCode.trim().isEmpty()) {
            return null;
        }
        
        // Trim spaces
        bookCode = bookCode.trim();
        
        // Tìm vị trí dấu gạch ngang cuối cùng
        int lastDashIndex = bookCode.lastIndexOf('-');
        if (lastDashIndex > 0) {
            return bookCode.substring(0, lastDashIndex).trim();
        }
        
        // Nếu không có dấu gạch ngang, coi toàn bộ là ISBN
        return bookCode;
    }
}
