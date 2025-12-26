package com.example.damh_library.fragment.client;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.BookListAdapter;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.response.BookListResponse;
import com.example.damh_library.model.response.MostBorrowBookResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.DauSachApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListFragment extends Fragment {

    private RecyclerView rvBooks;
    private SearchView svSearch;
    private LinearLayout llEmpty, llLoading, llPagination;
    private TextView tvEmpty, tvPageInfo;
    private ProgressBar progressBar;
    private Button btnPrevPage, btnNextPage;

    private BookListAdapter adapter;
    private List<MostBorrowBookResponse> bookList = new ArrayList<>();
    private DauSachApiService apiService;

    private TextView tvGenreButton;
    private LinearLayout llSelectedGenreTag;
    private List<String> allGenres = new ArrayList<>();
    private String selectedGenre = null;

    private PopupWindow currentPopup;

    private int currentPage = 1;
    private int totalPages = 1;
    private int pageSize = 5;
    private boolean isLoading = false;

    public BookListFragment() {}

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
        loadGenres();
        loadPage(1);
    }

    private void initViews(View view) {
        rvBooks = view.findViewById(R.id.rvBooks);
        svSearch = view.findViewById(R.id.svSearch);
        llEmpty = view.findViewById(R.id.llEmpty);
        llLoading = view.findViewById(R.id.llLoading);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        llPagination = view.findViewById(R.id.llPagination);
        btnPrevPage = view.findViewById(R.id.btnPrevPage);
        btnNextPage = view.findViewById(R.id.btnNextPage);
        tvPageInfo = view.findViewById(R.id.tvPageInfo);

        tvGenreButton = view.findViewById(R.id.tvGenreButton);
        llSelectedGenreTag = view.findViewById(R.id.llSelectedGenreTag);

        apiService = ApiClient.getClient().create(DauSachApiService.class);

        tvGenreButton.setOnClickListener(v -> showGenreSelectionPanel());

        btnPrevPage.setOnClickListener(v -> loadPage(currentPage - 1));
        btnNextPage.setOnClickListener(v -> loadPage(currentPage + 1));
    }

    private void setupRecyclerView() {
        adapter = new BookListAdapter(bookList);
        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBooks.setAdapter(adapter);
    }

    private void setupSearchView() {
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPage = 1;
                loadPage(1);
                svSearch.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentPage = 1;
                loadPage(1);
                return true;
            }
        });
    }

    private void loadGenres() {
        allGenres.clear();
        allGenres.add("Văn học");
        allGenres.add("Kinh tế");
        allGenres.add("Tâm lý");
        allGenres.add("Khoa học");
        allGenres.add("Lịch sử");
        allGenres.add("Truyện tranh");
        allGenres.add("Kỹ năng sống");
        allGenres.add("Tiểu thuyết");
        allGenres.add("Thiếu nhi");
        allGenres.add("Kinh dị");
        allGenres.add("Hài hước");

        Collections.sort(allGenres);
    }

    // showGenreSelectionPanel / helpers unchanged...

    private void showGenreSelectionPanel() {
        // unchanged (omitted for brevity)
        LinearLayout panel = new LinearLayout(requireContext());
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(32, 24, 32, 24);
        panel.setBackgroundColor(Color.WHITE);

        LinearLayout currentRow = null;
        int index = 0;

        for (String genre : allGenres) {
            if (index % 3 == 0) {
                currentRow = new LinearLayout(requireContext());
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                ((LinearLayout.LayoutParams) currentRow.getLayoutParams()).bottomMargin = 16;
                panel.addView(currentRow);
            }

            TextView tag = createGenreTagForPanel(genre);
            currentRow.addView(tag);
            index++;
        }

        currentPopup = new PopupWindow(panel,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        currentPopup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        currentPopup.setElevation(12);
        currentPopup.setOutsideTouchable(true);
        currentPopup.setFocusable(true);

        currentPopup.setTouchInterceptor((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_OUTSIDE) {
                currentPopup.dismiss();
                return true;
            }
            return false;
        });

        currentPopup.showAsDropDown(tvGenreButton, 0, 8);
    }

    private TextView createGenreTagForPanel(String genre) {
        TextView tag = new TextView(requireContext());
        tag.setText(genre);
        tag.setTextSize(14);
        tag.setPadding(28, 14, 28, 14);

        boolean isSelected = selectedGenre != null && selectedGenre.equals(genre);
        tag.setTextColor(isSelected ? Color.WHITE : Color.parseColor("#333333"));
        tag.setBackgroundResource(isSelected
                ? R.drawable.bg_number_badge
                : R.drawable.button_outlined_background);

        tag.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) tag.getLayoutParams()).setMargins(0, 0, 16, 0);

        tag.setOnClickListener(v -> {
            selectedGenre = genre;
            updateSelectedGenreTag();
            currentPage = 1;
            loadPage(1);

            if (currentPopup != null && currentPopup.isShowing()) {
                currentPopup.dismiss();
            }
        });

        return tag;
    }

    private void updateSelectedGenreTag() {
        llSelectedGenreTag.removeAllViews();

        if (selectedGenre != null) {
            View tagView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_selected_tag, llSelectedGenreTag, false);

            TextView tvTag = tagView.findViewById(R.id.tvTag);
            ImageButton btnRemove = tagView.findViewById(R.id.btnRemoveTag);

            tvTag.setText(selectedGenre);

            btnRemove.setOnClickListener(v -> {
                selectedGenre = null;
                updateSelectedGenreTag();
                currentPage = 1;
                loadPage(1);
            });

            llSelectedGenreTag.addView(tagView);
            llSelectedGenreTag.setVisibility(View.VISIBLE);
        } else {
            llSelectedGenreTag.setVisibility(View.GONE);
        }
    }

    // --- PHÂN TRANG CHÍNH ---
    private void loadPage(int page) {
        if (page < 1 || isLoading) return; // removed page > totalPages guard to allow loading when totalPages not known

        isLoading = true;
        showLoading(true);

        String search = svSearch.getQuery().toString().trim();

        apiService.getBooksPaginated(page, pageSize, selectedGenre, search)
                .enqueue(new Callback<ResponseSingleModel<BookListResponse>>() {
                    @Override
                    public void onResponse(Call<ResponseSingleModel<BookListResponse>> call,
                                           Response<ResponseSingleModel<BookListResponse>> response) {
                        isLoading = false;
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            BookListResponse data = response.body().getData();
                            if (data != null && data.getBooks() != null) {
                                // Always replace list with the current page's items (do not accumulate)
                                bookList.clear();
                                bookList.addAll(data.getBooks());

                                currentPage = data.getPagination().getCurrentPage();
                                totalPages = data.getPagination().getTotalPages();

                                updatePaginationUI();
                                updateUI();
                                return;
                            }
                        }
                        showEmptyState("Không tải được dữ liệu");
                    }

                    @Override
                    public void onFailure(Call<ResponseSingleModel<BookListResponse>> call, Throwable t) {
                        isLoading = false;
                        showLoading(false);
                        showEmptyState("Lỗi kết nối mạng");
                        Log.e("BookList", "Load page failed: " + t.getMessage());
                    }
                });
    }

    private void updatePaginationUI() {
        // Hide pagination when no items or only one page
        if (bookList.isEmpty() || totalPages <= 1) {
            llPagination.setVisibility(View.GONE);
        } else {
            llPagination.setVisibility(View.VISIBLE);
            tvPageInfo.setText("Trang " + currentPage + " / " + totalPages);
            btnPrevPage.setEnabled(currentPage > 1);
            btnNextPage.setEnabled(currentPage < totalPages);
        }
    }

    private void showLoading(boolean show) {
        llLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        rvBooks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(String message) {
        llEmpty.setVisibility(View.VISIBLE);
        rvBooks.setVisibility(View.GONE);
        tvEmpty.setText(message);
        // hide pagination when empty
        llPagination.setVisibility(View.GONE);
    }

    private void updateUI() {
        if (bookList.isEmpty()) {
            showEmptyState("Không tìm thấy sách");
        } else {
            llEmpty.setVisibility(View.GONE);
            // show/hide pagination is handled in updatePaginationUI()
            adapter.notifyDataSetChanged();
        }
    }

    public void refreshData() {
        svSearch.setQuery("", false);
        selectedGenre = null;
        updateSelectedGenreTag();
        loadPage(1);
    }
}
