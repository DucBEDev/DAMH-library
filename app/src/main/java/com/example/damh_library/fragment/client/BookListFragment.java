package com.example.damh_library.fragment.client;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.BookListAdapter;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.response.MostBorrowBookResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.DauSachApiService;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListFragment extends Fragment {

    private RecyclerView rvBooks;
    private SearchView svSearch;
    private LinearLayout llEmpty, llLoading;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private BookListAdapter adapter;
    private List<MostBorrowBookResponse> originalBookList = new ArrayList<>();
    private List<MostBorrowBookResponse> filteredBookList = new ArrayList<>();
    private DauSachApiService apiService;

    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupSearchView();
        loadBooks();
    }

    private void initViews(View view) {
        rvBooks = view.findViewById(R.id.rvBooks);
        svSearch = view.findViewById(R.id.svSearch);
        llEmpty = view.findViewById(R.id.llEmpty);
        llLoading = view.findViewById(R.id.llLoading);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        apiService = ApiClient.getClient().create(DauSachApiService.class);
    }

    private void setupRecyclerView() {
        adapter = new BookListAdapter(filteredBookList);
        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBooks.setAdapter(adapter);
        rvBooks.setHasFixedSize(true);
    }

    private void setupSearchView() {
        // Đảm bảo SearchView có thể focus
        svSearch.setFocusable(true);
        svSearch.setFocusableInTouchMode(true);
        svSearch.setClickable(true);

        // Set query hint
        svSearch.setQueryHint("Tìm kiếm sách, tác giả...");

        // Set màu sắc cho SearchView
        svSearch.setBackgroundResource(android.R.color.white);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                svSearch.clearFocus(); // Ẩn bàn phím sau khi search
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Real-time search khi user đang gõ
                performSearch(newText);
                return true;
            }
        });

        // Xử lý khi click vào SearchView
        svSearch.setOnSearchClickListener(v -> {
            Log.d("BookList", "Search clicked");
        });

        // Xử lý khi đóng SearchView
        svSearch.setOnCloseListener(() -> {
            performSearch(""); // Reset về danh sách gốc
            return false;
        });
    }

    private void loadBooks() {
        showLoading(true);

        Call<ResponseModel<MostBorrowBookResponse>> call = apiService.getMostQuantity(100);
        call.enqueue(new Callback<ResponseModel<MostBorrowBookResponse>>() {
            @Override
            public void onResponse(Call<ResponseModel<MostBorrowBookResponse>> call,
                                   Response<ResponseModel<MostBorrowBookResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel<MostBorrowBookResponse> responseModel = response.body();

                    if (responseModel.isSuccess() && responseModel.getData() != null) {
                        originalBookList.clear();
                        originalBookList.addAll(responseModel.getData());

                        // Reset filtered list
                        filteredBookList.clear();
                        filteredBookList.addAll(originalBookList);

                        Log.d("BookList", "Loaded " + originalBookList.size() + " books");
                        updateUI();

                        // Log thông tin sách để debug
                        for (MostBorrowBookResponse book : originalBookList) {
                            Log.d("BookList", "Book: " + book.getTitle() + " - " + book.getAuthor() + " - " + book.getType());
                        }
                    } else {
                        String message = responseModel.getMessage() != null ? responseModel.getMessage() : "Không có dữ liệu";
                        showEmptyState(message);
                        Toasty.info(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showEmptyState("Lỗi tải dữ liệu");
                    Toasty.error(requireContext(), "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<MostBorrowBookResponse>> call, Throwable t) {
                showLoading(false);
                showEmptyState("Lỗi kết nối mạng");
                Log.e("BookList", "API call failed: " + t.getMessage());
                Toasty.error(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        Log.d("BookList", "Searching for: " + query);

        filteredBookList.clear();

        if (TextUtils.isEmpty(query.trim())) {
            // Nếu query rỗng, hiển thị tất cả sách
            filteredBookList.addAll(originalBookList);
        } else {
            String searchQuery = query.toLowerCase().trim();

            // Filter books based on title, author, or type
            for (MostBorrowBookResponse book : originalBookList) {
                boolean matches = false;

                // Check title
                if (book.getTitle() != null && book.getTitle().toLowerCase().contains(searchQuery)) {
                    matches = true;
                }

                // Check author
                if (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(searchQuery)) {
                    matches = true;
                }

                // Check type/category
                if (book.getType() != null && book.getType().toLowerCase().contains(searchQuery)) {
                    matches = true;
                }

                if (matches) {
                    filteredBookList.add(book);
                }
            }
        }

        Log.d("BookList", "Search results: " + filteredBookList.size() + " books");
        updateUI();
    }

    private void showLoading(boolean show) {
        if (show) {
            llLoading.setVisibility(View.VISIBLE);
            rvBooks.setVisibility(View.GONE);
            llEmpty.setVisibility(View.GONE);
        } else {
            llLoading.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(String message) {
        llEmpty.setVisibility(View.VISIBLE);
        rvBooks.setVisibility(View.GONE);
        llLoading.setVisibility(View.GONE);
        tvEmpty.setText(message);
    }

    private void updateUI() {
        if (filteredBookList.isEmpty()) {
            String currentQuery = svSearch.getQuery().toString();
            String emptyMessage = TextUtils.isEmpty(currentQuery) ?
                    "Chưa có sách nào" :
                    "Không tìm thấy sách phù hợp với \"" + currentQuery + "\"";
            showEmptyState(emptyMessage);
        } else {
            llEmpty.setVisibility(View.GONE);
            llLoading.setVisibility(View.GONE);
            rvBooks.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    // Method để refresh data từ bên ngoài
    public void refreshData() {
        if (svSearch != null) {
            svSearch.setQuery("", false);
        }
        loadBooks();
    }
}
