package com.example.damh_library.fragment.client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.response.BookCartResponse;
import com.example.damh_library.model.response.BorrowedBookResponse;
import com.example.damh_library.model.response.OverdueCheckResponse;
import com.example.damh_library.model.response.ReaderCardResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.CheckoutSlipApiService;
import com.example.damh_library.network.client.DauSachApiService;
import com.example.damh_library.network.client.ReaderApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCartFragment extends Fragment {
    private static final String ARG_CART_TYPE = "cart_type";
    public static final String TYPE_PHYSICAL = "physical";
    public static final String TYPE_ONLINE = "online";

    private String cartType; // "physical" hoặc "online"

    private RecyclerView rvCartBooks;
    private LinearLayout llEmptyState, llSelectionHeader;
    private ImageButton btnBack;
    private CheckBox cbSelectAll;
    private TextView tvSelectedCount, tvTotalBooks;
    private Button btnCreateBorrowTicket;
    private BookCartAdapter adapter;
    private List<BookCartResponse> cartBooks;
    private ProgressBar progressLoading;
    private DauSachApiService dauSachApiService;
    private CheckoutSlipApiService checkoutSlipApiService;
    private CompoundButton.OnCheckedChangeListener selectAllListener;

    public static SubCartFragment newInstance(String type) {
        SubCartFragment fragment = new SubCartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CART_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_cart, container, false);

        initViews(view);
        setupRecyclerView();
        loadCartBooks();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cartType = getArguments().getString(ARG_CART_TYPE, TYPE_PHYSICAL);
        }
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

//        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnCreateBorrowTicket.setOnClickListener(v -> {
            List<BookCartResponse> selectedBooks = adapter.getSelectedBooks();
            checkConstraintsAndCreate(selectedBooks);
        });

        dauSachApiService = ApiClient.getClient().create(DauSachApiService.class);
        checkoutSlipApiService = ApiClient.getClient().create(CheckoutSlipApiService.class);
    }

    private void setupRecyclerView() {
        cartBooks = new ArrayList<>();
        adapter = new BookCartAdapter(requireContext(), cartBooks, this.cartType, new BookCartAdapter.OnCartItemListener() {
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
//        rvCartBooks.setHasFixedSize(false);
//        rvCartBooks.setNestedScrollingEnabled(false);
//        rvCartBooks.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);


        selectAllListener = (buttonView, isChecked) -> {
            if (adapter == null) return;

            if (isChecked) adapter.selectAll();
            else adapter.deselectAll();
        };

        cbSelectAll.setOnCheckedChangeListener(selectAllListener);

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
                            // Normalize ISBN and ensure imageUrl presence
                            if (b.getIsbn() != null) b.setIsbn(b.getIsbn());
                            if (b.getImageUrl() == null || b.getImageUrl().isEmpty()) b.setImageUrl(null);

                            boolean isOnlineBook = b.getMaSach().trim().endsWith("-ON");

                            if (TYPE_ONLINE.equals(cartType)) {
                                if (isOnlineBook) {
                                    cartBooks.add(b);
                                }
                            } else { // TYPE_PHYSICAL
                                if (!isOnlineBook) {
                                    cartBooks.add(b);
                                }
                            }
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
            // remove from the shared list, then let adapter adjust its selection indices and notify
            cartBooks.remove(position);
            adapter.onItemRemoved(position);

            Toasty.success(requireContext(), "Đã xóa \"" + book.getTitle() + "\" khỏi giỏ", Toast.LENGTH_SHORT).show();

            updateUI();
        }
    }

    // <-- CẬP NHẬT: Dùng service checkReaderBorrowStatus thay vì getBorrowedBooks + getCardInfo
    private void checkConstraintsAndCreate(List<BookCartResponse> selectedBooks) {
        if (selectedBooks.isEmpty()) {
            Toasty.warning(requireContext(), "Vui lòng chọn sách để tạo phiếu mượn", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBooks.size() > 3) {
            Toasty.warning(requireContext(), "Chỉ được mượn tối đa 3 cuốn", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("key_userId", "5");

        checkoutSlipApiService.checkReaderBorrowStatus(userId)
                .enqueue(new Callback<ResponseSingleModel<OverdueCheckResponse>>() {
                    @Override
                    public void onResponse(Call<ResponseSingleModel<OverdueCheckResponse>> call,
                                           Response<ResponseSingleModel<OverdueCheckResponse>> response) {

                        // <-- CẬP NHẬT: Luôn kiểm tra response.body() trước khi dùng
                        if (response.isSuccessful() && response.body() != null) {
                            ResponseSingleModel<OverdueCheckResponse> body = response.body();

                            if (body.isSuccess()) {
                                // Được phép mượn → mở dialog xác nhận
                                showCreateBorrowTicketDialogConfirmed(selectedBooks);
                            } else {
                                // Không được mượn → hiển thị thông báo phù hợp
                                String message = body.getMessage() != null ? body.getMessage() : "Không thể mượn sách";
                                OverdueCheckResponse data = body.getData();

                                List<OverdueCheckResponse.OverdueBookInfo> overdueList = null;
                                if (data != null) {
                                    overdueList = data.getOverdueBooks();
                                }

                                if (overdueList != null && !overdueList.isEmpty()) {
                                    // Có sách quá hạn → liệt kê chi tiết
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(message).append(":\n\n");
                                    for (OverdueCheckResponse.OverdueBookInfo book : overdueList) {
                                        sb.append("• ").append(book.getTenSach())
                                                .append(" - mã phiếu #").append(book.getMaPhieu())
                                                .append("\n");
                                    }
                                    sb.append("\nVui lòng trả sách quá hạn trước khi mượn mới.");

                                    new MaterialAlertDialogBuilder(requireContext())
                                            .setTitle("Không thể mượn sách")
                                            .setMessage(sb.toString())
                                            .setPositiveButton("Đóng", null)
                                            .show();
                                } else {
                                    // Lỗi khác (hết hạn thẻ, khóa thẻ, v.v.)
                                    new MaterialAlertDialogBuilder(requireContext())
                                            .setTitle("Không thể mượn sách")
                                            .setMessage(message)
                                            .setPositiveButton("Đóng", null)
                                            .show();
                                }
                            }
                        } else {
                            // HTTP lỗi hoặc body null → thông báo chung
                            String errorMsg = response.message();
                            if (response.code() >= 400 && response.code() < 600) {
                                errorMsg = "Lỗi server (" + response.code() + ")";
                            }
                            Toasty.error(requireContext(),
                                    "Không thể kiểm tra trạng thái: " + errorMsg,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseSingleModel<OverdueCheckResponse>> call, Throwable t) {
                        Log.e("AAA", "Lỗi kết nối: " + t.getMessage());
                        Toasty.error(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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

    private void showCreateBorrowTicketDialogConfirmed(List<BookCartResponse> selectedBooks) {
        // Build message and confirm
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
        if (selectedBooks.isEmpty()) {
            Toasty.warning(requireContext(), "Vui lòng chọn sách để tạo phiếu mượn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Open CreateCheckoutFragment and pass selected books
        CreateCheckoutFragment fragment = new CreateCheckoutFragment(selectedBooks, cartType);

        // Replace `R.id.fragment_container` with your actual container id (e.g. R.id.nav_host_fragment)
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentClientDashboard, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateSelectionUI(List<BookCartResponse> selectedBooks) {
        // Use adapter's counts to ensure consistency (avoid mismatch between fragment local counting and adapter state)
        if (adapter == null) return;

        int selectedCount = adapter.getSelectedCount();
        int selectableCount = adapter.getSelectableCount();

        tvSelectedCount.setText("Đã chọn: " + selectedCount);

        // Update select all checkbox (temporarily detach listener while programmatically changing checked state)
        cbSelectAll.setOnCheckedChangeListener(null);

        if (selectedCount == 0) {
            cbSelectAll.setChecked(false);
            cbSelectAll.setAlpha(1f);
        } else if (selectedCount == selectableCount) {
            cbSelectAll.setChecked(true);
            cbSelectAll.setAlpha(1f);
        } else {
            cbSelectAll.setChecked(false);
        }

        cbSelectAll.setOnCheckedChangeListener(selectAllListener);


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