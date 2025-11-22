package com.example.damh_library.adapter.client;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.model.response.BookDetailResponse;

import java.util.List;

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
            holder.ivBorrowStatusIcon.setImageResource(R.drawable.ic_error); // hoặc ic_borrowed
            holder.ivBorrowStatusIcon.setImageTintList(context.getColorStateList(R.color.gray_light));
            holder.llBorrowStatus.setBackgroundResource(R.drawable.bg_available_badge); // tạo nếu cần
            holder.llBorrowStatus.setBackgroundTintList(context.getColorStateList(R.color.error));
        } else {
            holder.ivBorrowStatusIcon.setImageResource(R.drawable.ic_check_circle);
            holder.ivBorrowStatusIcon.setImageTintList(context.getColorStateList(R.color.success));
            holder.llBorrowStatus.setBackgroundResource(R.drawable.bg_available_badge);
        }

    }

    @Override
    public int getItemCount() {
        return bookCopies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View llBorrowStatus;
        TextView tvMaSach, tvStatus, tvBorrowStatus;
        ImageView ivStatusIcon, ivBorrowStatusIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaSach = itemView.findViewById(R.id.tvMaSach);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBorrowStatus = itemView.findViewById(R.id.tvBorrowStatus);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            ivBorrowStatusIcon = itemView.findViewById(R.id.ivBorrowStatusIcon);
            llBorrowStatus = itemView.findViewById(R.id.llBorrowStatus);
        }
    }
}