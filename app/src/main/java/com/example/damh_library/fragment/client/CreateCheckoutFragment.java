package com.example.damh_library.fragment.client;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.request.PhieuMuonRequest;
import com.example.damh_library.model.response.BookCartResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.CheckoutSlipApiService;
import com.example.damh_library.utils.PhieuMuonPaymentHelper;
import com.example.damh_library.model.request.PhieuMuonPaymentRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCheckoutFragment extends Fragment {

    private List<BookCartResponse> selectedBooks;
    private SelectedBookCheckoutAdapter adapter;
    
    // Payment helper for online borrow
    private PhieuMuonPaymentHelper paymentHelper;

    // Views từ layout mới
    private MaterialButtonToggleGroup toggleGroupBorrowType;
    private MaterialButton btnInPlace;
    private MaterialButton btnTakeHome;
    private MaterialButton btnOnlineBorrow;
    private MaterialButton btnConfirmBorrow;
    private MaterialButton btnCancel;
    private TextView tvReaderName, tvReaderId, tvDate, tvTotalBooks;
    private RecyclerView rvSlipDetails;

    private String cartType;

    public CreateCheckoutFragment() {}

//    public CreateCheckoutFragment(List<BookCartResponse> selectedBooks) {
//        this.selectedBooks = selectedBooks;
//    }

    public CreateCheckoutFragment(List<BookCartResponse> selectedBooks, String cartType) {
        this.selectedBooks = selectedBooks;
        this.cartType=cartType;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_checkout, container, false);

        // Initialize payment helper
        paymentHelper = new PhieuMuonPaymentHelper(requireContext());

        // Ánh xạ các view mới
        rvSlipDetails = view.findViewById(R.id.rvSlipDetails);
        toggleGroupBorrowType = view.findViewById(R.id.toggleGroupBorrowType);
        btnInPlace = view.findViewById(R.id.btnInPlace);
        btnTakeHome = view.findViewById(R.id.btnTakeHome);
        btnOnlineBorrow = view.findViewById(R.id.btnOnlineBorrow);
        btnConfirmBorrow = view.findViewById(R.id.btnConfirmBorrow);
        btnCancel = view.findViewById(R.id.btnCancel);

        tvReaderName = view.findViewById(R.id.tvReaderName);
        tvReaderId = view.findViewById(R.id.tvReaderId);
        tvDate = view.findViewById(R.id.tvDate);
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks);

        // Thiết lập thông tin độc giả & ngày lập
        setupUserInfo();

        // Thiết lập danh sách sách
        setupBookList();

        // Logic chọn hình thức mượn
        setupBorrowTypeLogic();

        // Nút hành động
        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());

        btnConfirmBorrow.setOnClickListener(v -> {

            Boolean hinhThuc= null;
            if(toggleGroupBorrowType.getCheckedButtonId() == R.id.btnTakeHome)
            {
                hinhThuc = true;
            }
            else if(toggleGroupBorrowType.getCheckedButtonId() == R.id.btnInPlace)
            {
                hinhThuc = false;
            }
            if (hinhThuc!=null && hinhThuc==true && selectedBooks.size() > 3) {
                Toasty.error(requireContext(),
                        "Không thể mượn mang về quá 3 cuốn!", Toasty.LENGTH_LONG).show();
                return;
            }

            callCreatePhieuMuonApi(hinhThuc);
        });

        return view;
    }

    private void setupUserInfo() {
        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("key_userId", "");
        String fullName = prefs.getString("key_username", "Chưa đăng nhập");

        tvReaderId.setText(userId);
        tvReaderName.setText(fullName);

        // Ngày hiện tại (theo yêu cầu: 17/11/2025)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));
    }

    private void setupBookList() {
        tvTotalBooks.setText(selectedBooks.size() + " cuốn");

        adapter = new SelectedBookCheckoutAdapter(requireContext(),cartType ,selectedBooks);
        rvSlipDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSlipDetails.setAdapter(adapter);
    }

    private void setupBorrowTypeLogic() {
        if (cartType!=null && cartType.equalsIgnoreCase(SubCartFragment.TYPE_ONLINE))
        {
            btnInPlace.setEnabled(false);
            btnTakeHome.setEnabled(false);
            toggleGroupBorrowType.check(R.id.btnOnlineBorrow);
        }

        else
        {
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

    private void callCreatePhieuMuonApi(Boolean hinhThuc) {
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        long maDG = Long.parseLong(prefs.getString("key_userId", "0")); // Lấy maDG
        long maNV = 4; // Giả định maNV (thủ thư)

        // Xác định thông báo thành công theo hình thức mượn
        String successMessage;
        if (hinhThuc == null) {
            successMessage = "Đã tạo phiếu mượn trực tuyến thành công!";
        } else if (hinhThuc) {
            successMessage = "Đã tạo phiếu mượn mang về thành công!";
        } else {
            successMessage = "Đã tạo phiếu mượn tại chỗ thành công!";
        }
        
        // Nếu là mượn online (hinhThuc == null), dùng payment
        if (hinhThuc == null && cartType != null && cartType.equalsIgnoreCase(SubCartFragment.TYPE_ONLINE)) {
            callCreatePhieuMuonWithPayment(maDG, maNV, successMessage);
            return;
        }
        
        // Mượn tại chỗ hoặc mang về - không cần payment
        PhieuMuonRequest request = new PhieuMuonRequest( maDG, hinhThuc, maNV, selectedBooks);
        CheckoutSlipApiService service = ApiClient.getClient().create(CheckoutSlipApiService.class);
        Call<ResponseModel<Void>> call = service.createCheckoutWithRequest(request);
        call.enqueue(new Callback<ResponseModel<Void>>() {
            @Override
            public void onResponse(Call<ResponseModel<Void>> call, Response<ResponseModel<Void>> response) {
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toasty.success(requireContext(),
                            successMessage,
                            Toasty.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Lỗi không xác định";
                    Toasty.error(requireContext(), "Lỗi tạo phiếu mượn: " + errorMsg, Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<Void>> call, Throwable t) {
                Toasty.error(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }
    

    // Lập phiếu mượn online với thanh toán
    private void callCreatePhieuMuonWithPayment(long maDG, long maNV, String successMessage) {
        // Disable button để tránh double click
        btnConfirmBorrow.setEnabled(false);
        btnConfirmBorrow.setText("Đang xử lý...");
        
        // Tính phí mượn sách (giả sử mỗi cuốn 10,000 VND)
        int feePerBook = 10000;
        int totalAmount = selectedBooks.size() * feePerBook;
        
        // Prepare danh sách sách
        java.util.ArrayList<PhieuMuonPaymentRequest.SachItem> danhSachSach = new java.util.ArrayList<>();
        for (BookCartResponse book : selectedBooks) {
            danhSachSach.add(new PhieuMuonPaymentRequest.SachItem(book.getMaSach(), true));
        }
        
        // Create payment request
        PhieuMuonPaymentRequest paymentRequest = new PhieuMuonPaymentRequest(
            (int) maDG,
            true, // hinhThuc = true for online borrow
            (int) maNV,
            danhSachSach,
            totalAmount
        );
        
        // Start payment flow
        paymentHelper.startPaymentFlow(paymentRequest, new PhieuMuonPaymentHelper.PaymentCallback() {
            @Override
            public void onSuccess(long orderCode) {
                // Payment thành công & phiếu mượn đã được tạo
                requireActivity().runOnUiThread(() -> {
                    Toasty.success(requireContext(), 
                        successMessage + " (Mã đơn: " + orderCode + ")", 
                        Toasty.LENGTH_LONG).show();
                    
                    // Back to previous screen
                    requireActivity().onBackPressed();
                });
            }
            
            @Override
            public void onFailure(String message) {
                // Payment thất bại
                requireActivity().runOnUiThread(() -> {
                    btnConfirmBorrow.setEnabled(true);
                    btnConfirmBorrow.setText("Xác nhận");
                    
                    Toasty.error(requireContext(), 
                        "Lỗi thanh toán: " + message, 
                        Toasty.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onPaymentPending(String status) {
                // Đang chờ thanh toán
                requireActivity().runOnUiThread(() -> {
                    btnConfirmBorrow.setText("Đang chờ thanh toán...");
                });
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cleanup payment helper
        if (paymentHelper != null) {
            paymentHelper.destroy();
        }
    }
}