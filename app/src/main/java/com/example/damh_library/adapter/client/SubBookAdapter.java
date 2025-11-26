package com.example.damh_library.adapter.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.request.BookCartRequest;
import com.example.damh_library.model.request.PhieuMuonRequest;
import com.example.damh_library.model.response.BookDetailResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.CheckoutSlipApiService;
import com.example.damh_library.network.client.DauSachApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubBookAdapter extends RecyclerView.Adapter<SubBookAdapter.ViewHolder> {

    private Context context;
    private List<BookDetailResponse.BookCopy> bookCopies;

    public SubBookAdapter(Context context, List<BookDetailResponse.BookCopy> bookCopies) {
        this.context = context;
        this.bookCopies = bookCopies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sub_book_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookDetailResponse.BookCopy copy = bookCopies.get(position);

        holder.tvMaSach.setText("Mã sách: " + copy.getMaSach());

        // Tình trạng sách (Tốt / Hỏng)
        boolean isGood = copy.isTinhTrangSach();
        holder.tvStatus.setText(isGood ? "Mới" : "Cũ");
        int statusColor = isGood ? R.color.success : R.color.error;
        holder.tvStatus.setTextColor(context.getColor(statusColor));
        holder.ivStatusIcon.setBackgroundTintList(context.getColorStateList(statusColor));

        // Trạng thái mượn
        boolean dangMuon = copy.isDangMuon();
        holder.tvBorrowStatus.setText(dangMuon ? "Đang mượn" : "Có thể mượn");
        int borrowColor = dangMuon ? R.color.gray_light : R.color.success;
        holder.tvBorrowStatus.setTextColor(context.getColor(borrowColor));

        // Icon và background cho trạng thái mượn
        if (dangMuon) {
            // Đang mượn → nền xám + ẩn nút + hiển thị trạng thái
            holder.cardItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray_light));
            holder.cardItem.setClickable(false);
            holder.cardItem.setFocusable(false);

            holder.tvBorrowStatus.setText("Đang mượn");
            holder.tvBorrowStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.ivBorrowStatusIcon.setImageResource(R.drawable.ic_error);
            holder.ivBorrowStatusIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.text_secondary));
            holder.llBorrowStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray_medium));

            holder.btnAddToCart.setVisibility(View.GONE);
        } else {
            // Có thể mượn → nền trắng + hiện nút thêm
            holder.cardItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.cardItem.setClickable(true);
            holder.cardItem.setFocusable(true);

            holder.tvBorrowStatus.setText("Có thể mượn");
            holder.tvBorrowStatus.setTextColor(ContextCompat.getColor(context, R.color.success));
            holder.ivBorrowStatusIcon.setImageResource(R.drawable.ic_check_circle);
            holder.ivBorrowStatusIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.success));
//            holder.llBorrowStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.success));

            holder.btnAddToCart.setVisibility(View.VISIBLE);
        }

        // Nút thêm vào giỏ
        holder.btnAddToCart.setOnClickListener(v -> {
            AddBookToCartService(copy.getMaSach().trim());
        });

    }

    private void AddBookToCartService(String maSach) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        long maDG = Long.parseLong(prefs.getString("key_userId", "0"));

        BookCartRequest request = new BookCartRequest( maDG, maSach);
        DauSachApiService service = ApiClient.getClient().create(DauSachApiService.class);
        Call<ResponseModel<Void>> call = service.addBookToCart(request);
        call.enqueue(new Callback<ResponseModel<Void>>() {
            @Override
            public void onResponse(Call<ResponseModel<Void>> call, Response<ResponseModel<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toasty.success(context,
                             response.body().getMessage(),
                            Toasty.LENGTH_LONG).show();

                } else {
                    // Lỗi HTTP (400, 500, v.v.) → đọc errorBody
                    String errorMessage = "Lỗi không xác định";

                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            // Parse JSON lỗi thủ công (vì ResponseModel<Void> không match)
                            if (errorJson.contains("message")) {
                                // Dùng regex đơn giản hoặc Gson
                                errorMessage = errorJson.split("\"message\":\"")[1].split("\"")[0];
                            } else {
                                errorMessage = errorJson;
                            }
                        } catch (Exception e) {
                            errorMessage = "Lỗi server";
                        }
                    }

                    Toasty.warning(context, errorMessage, Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<Void>> call, Throwable t) {
                Toasty.error(context, "Lỗi kết nối: " + t.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookCopies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardItem;
        TextView tvMaSach, tvStatus, tvBorrowStatus;
        ImageView ivBorrowStatusIcon, ivStatusIcon;
        LinearLayout llStatus, llBorrowStatus;
        MaterialButton btnAddToCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardItem = itemView.findViewById(R.id.cardItem);
            tvMaSach = itemView.findViewById(R.id.tvMaSach);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBorrowStatus = itemView.findViewById(R.id.tvBorrowStatus);
            ivBorrowStatusIcon = itemView.findViewById(R.id.ivBorrowStatusIcon);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            llStatus = itemView.findViewById(R.id.llStatus);
            llBorrowStatus = itemView.findViewById(R.id.llBorrowStatus);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}