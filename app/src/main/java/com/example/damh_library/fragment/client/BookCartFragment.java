package com.example.damh_library.fragment.client;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
import com.example.damh_library.adapter.client.BookCartAdapter;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.response.BookCartResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.DauSachApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookCartFragment extends Fragment {

    private RecyclerView rvCartBooks;
    private LinearLayout llEmptyState, llSelectionHeader;
    private ImageButton btnBack;
    private CheckBox cbSelectAll;
    private TextView tvSelectedCount, tvTotalBooks;
    private MaterialButton btnCreateBorrowTicket;
    private BookCartAdapter adapter;
    private List<BookCartResponse> cartBooks;
    private ProgressBar progressLoading;
    private DauSachApiService dauSachApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_cart, container, false);

        initViews(view);
        setupRecyclerView();
        loadCartBooks();

        return view;
    }

    private void initViews(View view) {
        rvCartBooks = view.findViewById(R.id.rvCartBooks);
        llEmptyState = view.findViewById(R.id.llEmptyState);
        llSelectionHeader = view.findViewById(R.id.llSelectionHeader);
        btnBack = view.findViewById(R.id.btnBack);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        tvSelectedCount = view.findViewById(R.id.tvSelectedCount);
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks);
        btnCreateBorrowTicket = view.findViewById(R.id.btnCreateBorrowTicket);
        progressLoading = view.findViewById(R.id.progressLoading);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (adapter != null) {
                if (isChecked) {
                    adapter.selectAll();
                } else {
                    adapter.deselectAll();
                }
            }
        });

        btnCreateBorrowTicket.setOnClickListener(v -> showCreateBorrowTicketDialog());

        dauSachApiService = ApiClient.getClient().create(DauSachApiService.class);
    }

    private void setupRecyclerView() {
        cartBooks = new ArrayList<>();
        adapter = new BookCartAdapter(requireContext(), cartBooks, new BookCartAdapter.OnCartItemListener() {
            @Override
            public void onBookClick(BookCartResponse book) {
                // TODO: Mở chi tiết sách
                Toasty.info(requireContext(), "Chi tiết: " + book.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRemoveFromCart(BookCartResponse book, int position) {
                showRemoveConfirmation(book, position);
            }

            @Override
            public void onSelectionChanged(List<BookCartResponse> selectedBooks) {
                updateSelectionUI(selectedBooks);
            }
        });

        rvCartBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCartBooks.setAdapter(adapter);
    }

    private void loadCartBooks() {
        showLoading(true);

        String userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("key_userId", "5");

        Call<ResponseModel<BookCartResponse>> call = dauSachApiService.getFavoriteBooks(userId);
        call.enqueue(new Callback<ResponseModel<BookCartResponse>>() {
            @Override
            public void onResponse(Call<ResponseModel<BookCartResponse>> call, Response<ResponseModel<BookCartResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel<BookCartResponse> body = response.body();
                    if (body.isSuccess() && body.getData() != null && !body.getData().isEmpty()) {
                        cartBooks.clear();
                        // body.getData() is List<BookCartResponse>
                        for (BookCartResponse b : body.getData()) {
                            // Ensure fields from backend map correctly (trim ISBN, fallback image)
                            if (b.getIsbn() == null || b.getIsbn().isEmpty()) {
                                // try to read ISBN from other fields if necessary (not available here)
                            }
                            cartBooks.add(b);
                        }
                        updateUI();
                    } else {
                        cartBooks.clear();
                        updateUI();
                        Toasty.info(requireContext(), body.getMessage() != null ? body.getMessage() : "Không có sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    cartBooks.clear();
                    updateUI();
                    Toasty.error(requireContext(), "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<BookCartResponse>> call, Throwable t) {
                showLoading(false);
                cartBooks.clear();
                updateUI();
                Toasty.error(requireContext(), "Không thể kết nối tới server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRemoveConfirmation(BookCartResponse book, int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa khỏi giỏ sách")
                .setMessage("Bạn có chắc muốn xóa \"" + book.getTitle() + "\" khỏi giỏ sách?")
                .setPositiveButton("Xóa", (dialog, which) -> removeFromCart(book, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void removeFromCart(BookCartResponse book, int position) {
        // TODO: Call API to remove from cart
        // dauSachApiService.removeFromCart(userId, book.getIsbn()).enqueue(callback);

        if (position >= 0 && position < cartBooks.size()) {
            cartBooks.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, cartBooks.size());

            Toasty.success(requireContext(), "Đã xóa \"" + book.getTitle() + "\" khỏi giỏ", Toast.LENGTH_SHORT).show();

            updateUI();
        }
    }

    private void showCreateBorrowTicketDialog() {
        List<BookCartResponse> selectedBooks = adapter.getSelectedBooks();

        if (selectedBooks.isEmpty()) {
            Toasty.warning(requireContext(), "Vui lòng chọn sách để tạo phiếu mượn", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder("Bạn đang tạo phiếu mượn cho:\n\n");
        for (int i = 0; i < selectedBooks.size(); i++) {
            message.append((i + 1)).append(". ").append(selectedBooks.get(i).getTitle()).append("\n");
        }
        message.append("\nTổng: ").append(selectedBooks.size()).append(" cuốn sách");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận tạo phiếu mượn")
                .setMessage(message.toString())
                .setPositiveButton("Tạo phiếu", (dialog, which) -> createBorrowTicket(selectedBooks))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createBorrowTicket(List<BookCartResponse> selectedBooks) {
        // TODO: Call API to create borrow ticket
        // dauSachApiService.createBorrowTicket(userId, bookIds).enqueue(callback);

        Toasty.success(requireContext(), "Đang tạo phiếu mượn cho " + selectedBooks.size() + " cuốn sách...", Toast.LENGTH_LONG).show();

        // Navigate to borrow ticket screen or refresh
        // For now, just clear selection
        adapter.deselectAll();
    }

    private void updateSelectionUI(List<BookCartResponse> selectedBooks) {
        int selectedCount = selectedBooks.size();
        tvSelectedCount.setText("Đã chọn: " + selectedCount);

        // Update select all checkbox
        cbSelectAll.setOnCheckedChangeListener(null);
        cbSelectAll.setChecked(adapter.isAllSelected());
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (adapter != null) {
                if (isChecked) {
                    adapter.selectAll();
                } else {
                    adapter.deselectAll();
                }
            }
        });

        // Enable/disable create button
        btnCreateBorrowTicket.setEnabled(selectedCount > 0);
    }

    private void showLoading(boolean show) {
        if (show) {
            progressLoading.setVisibility(View.VISIBLE);
            rvCartBooks.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.GONE);
            llSelectionHeader.setVisibility(View.GONE);
        } else {
            progressLoading.setVisibility(View.GONE);
        }
    }

    private void updateUI() {
        if (cartBooks.isEmpty()) {
            rvCartBooks.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
            llSelectionHeader.setVisibility(View.GONE);
            tvTotalBooks.setText("0 cuốn");
            btnCreateBorrowTicket.setEnabled(false);
        } else {
            rvCartBooks.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
            llSelectionHeader.setVisibility(View.VISIBLE);
            tvTotalBooks.setText(cartBooks.size() + " cuốn");
            adapter.notifyDataSetChanged();
        }
    }
}