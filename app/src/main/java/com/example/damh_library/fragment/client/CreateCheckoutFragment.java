package com.example.damh_library.fragment.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.SelectedBookCheckoutAdapter;
import com.example.damh_library.model.response.BookCartResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class CreateCheckoutFragment extends Fragment {

    private List<BookCartResponse> selectedBooks;
    private SelectedBookCheckoutAdapter adapter;

    // Views từ layout mới
    private MaterialButtonToggleGroup toggleGroupBorrowType;
    private MaterialButton btnInPlace;
    private MaterialButton btnTakeHome;
    private MaterialButton btnConfirmBorrow;
    private MaterialButton btnCancel;
    private TextView tvReaderName, tvReaderId, tvDate, tvTotalBooks;
    private RecyclerView rvSlipDetails;

    public CreateCheckoutFragment() {}

    public CreateCheckoutFragment(List<BookCartResponse> selectedBooks) {
        this.selectedBooks = selectedBooks;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_checkout, container, false);

        // Ánh xạ các view mới
        rvSlipDetails = view.findViewById(R.id.rvSlipDetails);
        toggleGroupBorrowType = view.findViewById(R.id.toggleGroupBorrowType);
        btnInPlace = view.findViewById(R.id.btnInPlace);
        btnTakeHome = view.findViewById(R.id.btnTakeHome);
        btnConfirmBorrow = view.findViewById(R.id.btnConfirmBorrow);
        btnCancel = view.findViewById(R.id.btnCancel);

        tvReaderName = view.findViewById(R.id.tvReaderName);
        tvReaderId = view.findViewById(R.id.tvReaderId);
        tvDate = view.findViewById(R.id.tvDate);
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks);

        // === 1. Thiết lập thông tin độc giả & ngày lập ===
        setupUserInfo();

        // === 2. Thiết lập danh sách sách ===
        setupBookList();

        // === 3. Logic chọn hình thức mượn ===
        setupBorrowTypeLogic();

        // === 4. Nút hành động ===
        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());

        btnConfirmBorrow.setOnClickListener(v -> {
            boolean isTakeHome = (toggleGroupBorrowType.getCheckedButtonId() == R.id.btnTakeHome);

            if (isTakeHome && selectedBooks.size() > 3) {
                Toasty.error(requireContext(),
                        "Không thể mượn mang về quá 3 cuốn!", Toasty.LENGTH_LONG).show();
                return;
            }

            // TODO: Gọi API tạo phiếu mượn ở đây
            Toasty.success(requireContext(),
                    isTakeHome ? "Đã tạo phiếu mượn mang về thành công!" : "Đã tạo phiếu mượn tại chỗ!",
                    Toasty.LENGTH_LONG).show();

            requireActivity().onBackPressed();
        });

        return view;
    }

    private void setupUserInfo() {
        // Giả lập thông tin người dùng (sau này lấy từ SharedPreferences hoặc API)
        String userId = "DG" + System.currentTimeMillis() % 10000;
        String fullName = "Nguyễn Văn A"; // Lấy từ login session

        tvReaderId.setText(userId);
        tvReaderName.setText(fullName);

        // Ngày hiện tại (theo yêu cầu: 17/11/2025)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));
    }

    private void setupBookList() {
        tvTotalBooks.setText(selectedBooks.size() + " cuốn");

        adapter = new SelectedBookCheckoutAdapter(requireContext(), selectedBooks);
        rvSlipDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSlipDetails.setAdapter(adapter);
    }

    private void setupBorrowTypeLogic() {
        boolean canTakeHome = selectedBooks.size() <= 3;

        // Mặc định chọn "Mượn tại chỗ"
        toggleGroupBorrowType.check(R.id.btnInPlace);

        // Nếu chọn >3 sách → disable hoàn toàn nút "Mang về"
        btnTakeHome.setEnabled(canTakeHome);

        if (!canTakeHome) {
            Toasty.info(requireContext(),
                    "Bạn đã chọn " + selectedBooks.size() + " cuốn. Chỉ được mang về tối đa 3 cuốn.",
                    Toasty.LENGTH_LONG).show();
        }

        toggleGroupBorrowType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;

            if (checkedId == R.id.btnTakeHome && selectedBooks.size() > 3) {
                Toasty.warning(requireContext(),
                        "Không thể chọn mang về khi quá 3 cuốn!", Toasty.LENGTH_LONG).show();
                toggleGroupBorrowType.check(R.id.btnInPlace);
            }
        });
    }
}