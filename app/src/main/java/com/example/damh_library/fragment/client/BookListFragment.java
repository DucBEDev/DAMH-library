package com.example.damh_library.fragment.client;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.BookListAdapter;
import com.example.damh_library.adapter.client.MostBorrowBookAdapter;
import com.example.damh_library.model.response.MostBorrowBookResponse;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.DauSachApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookListFragment extends Fragment {

    private SearchView svSearch;
    private RecyclerView rvBooks;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private BookListAdapter adapter;
    private List<MostBorrowBookResponse> allBooks = new ArrayList<>();
    private List<MostBorrowBookResponse> currentDisplayed = new ArrayList<>();

    private static final String BASE_URL = "https://your.api.url/"; // <- replace with real base URL
    private static final int FETCH_LIMIT = 1000; // fetch a large batch then paginate client-side
    private static final int PAGE_SIZE = 10;

    private int currentPage = 0;
    private boolean isLoadingPage = false;
    private boolean allLoaded = false;

    public BookListFragment() { }

    public static BookListFragment newInstance() {
        return new BookListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_book_list, container, false);

        svSearch = root.findViewById(R.id.svSearch);
        rvBooks = root.findViewById(R.id.rvBooks);
        progressBar = root.findViewById(R.id.progressBar);
        tvEmpty = root.findViewById(R.id.tvEmpty);

        adapter = new BookListAdapter(currentDisplayed);
        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBooks.setAdapter(adapter);

        rvBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0 || isLoadingPage || allLoaded) return;
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm == null) return;
                int visible = lm.getChildCount();
                int total = lm.getItemCount();
                int first = lm.findFirstVisibleItemPosition();
                if (first + visible >= total - 2) {
                    loadNextPage();
                }
            }
        });

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                applyFilter(query);
                return true;
            }

            @Override public boolean onQueryTextChange(String newText) {
                // small debounce
                new Handler().removeCallbacksAndMessages(null);
                new Handler().postDelayed(() -> applyFilter(newText), 250);
                return true;
            }
        });

        fetchBooks();

        return root;
    }

    private void fetchBooks() {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DauSachApiService mostBorrowBookApiService = ApiClient.getClient().create(DauSachApiService.class);
        Call<ResponseModel<MostBorrowBookResponse>> call = mostBorrowBookApiService.getMostQuantity(FETCH_LIMIT);

        call.enqueue(new Callback<ResponseModel<MostBorrowBookResponse>>() {
            @Override public void onResponse(Call<ResponseModel<MostBorrowBookResponse>> call, Response<ResponseModel<MostBorrowBookResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<MostBorrowBookResponse> data = response.body().getData();
                    if (data == null) data = new ArrayList<>();

                    allBooks.clear();
                    allBooks.addAll(data);
                    resetPaginationAndDisplay(allBooks);
                } else {
                    allBooks.clear();
                    resetPaginationAndDisplay(allBooks);
                }
            }

            @Override public void onFailure(Call<ResponseModel<MostBorrowBookResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                allBooks.clear();
                resetPaginationAndDisplay(allBooks);
            }
        });
    }

    private void resetPaginationAndDisplay(List<MostBorrowBookResponse> source) {
        currentPage = 0;
        allLoaded = false;
        currentDisplayed.clear();
        adapter.notifyDataSetChanged();
        if (source.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            loadNextPage();
        }
    }

    private void loadNextPage() {
        if (isLoadingPage || allLoaded) return;
        isLoadingPage = true;
        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allBooks.size());
        if (start >= end) {
            allLoaded = true;
            isLoadingPage = false;
            return;
        }

        List<MostBorrowBookResponse> page = allBooks.subList(start, end);
        int insertPos = currentDisplayed.size();
        currentDisplayed.addAll(page);
        adapter.notifyItemRangeInserted(insertPos, page.size());

        currentPage++;
        isLoadingPage = false;

        if (currentDisplayed.size() >= allBooks.size()) {
            allLoaded = true;
        }
    }

    private void applyFilter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<MostBorrowBookResponse> filtered;
        if (TextUtils.isEmpty(q)) {
            filtered = new ArrayList<>(allBooks); // unfiltered
        } else {
            filtered = new ArrayList<>();
            for (MostBorrowBookResponse b : allBooks) {
                String title = b.getTitle() == null ? "" : b.getTitle().toLowerCase();
                String author = b.getAuthor() == null ? "" : b.getAuthor().toLowerCase();
                if (title.contains(q) || author.contains(q)) filtered.add(b);
            }
        }

        // Replace allBooks with filtered list for paging display
        allBooks = filtered;
        resetPaginationAndDisplay(allBooks);
    }
}
