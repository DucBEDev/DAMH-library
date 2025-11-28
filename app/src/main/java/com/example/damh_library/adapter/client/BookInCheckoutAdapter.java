package com.example.damh_library.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.model.response.BookInCheckoutResponse;

import java.util.List;

public class BookInCheckoutAdapter extends RecyclerView.Adapter<BookInCheckoutAdapter.BookViewHolder> {

    private Context context;
    private List<BookInCheckoutResponse> books;
    private OnBookClickListener onBookClickListener;

    // Interface callback cho click event
    public interface OnBookClickListener {
        void onBookClick(BookInCheckoutResponse book);
    }

    public BookInCheckoutAdapter(Context context, List<BookInCheckoutResponse> books, OnBookClickListener listener) {
        this.context = context;
        this.books = books;
        this.onBookClickListener = listener;
    }

    // Constructor không có listener (backward compatibility)
    public BookInCheckoutAdapter(Context context, List<BookInCheckoutResponse> books) {
        this.context = context;
        this.books = books;
        this.onBookClickListener = null;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_in_checkout, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookInCheckoutResponse book = books.get(position);

        // Set book info
        holder.tvBookTitle.setText(book.getTenSach() != null ? book.getTenSach() : "Không có tên");
        holder.tvAuthor.setText(book.getTacGia() != null ? book.getTacGia() : "Không rõ tác giả");
        holder.tvBookCode.setText(book.getMaSach() != null ? book.getMaSach() : "N/A");

        // Load book cover
        if (book.getHinhAnh() != null && !book.getHinhAnh().isEmpty()) {
            Glide.with(context)
                    .load(book.getHinhAnh())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(holder.ivBookCover);
        } else {
            holder.ivBookCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        // Set status badge
        if (book.isTinhTrangTra()) {
            holder.tvStatus.setText("Đã trả");
            holder.tvStatus.setTextColor(context.getColor(R.color.success));
            holder.cardStatus.setCardBackgroundColor(context.getColor(R.color.success_light));
        } else {
            holder.tvStatus.setText("Đang mượn");
            holder.tvStatus.setTextColor(context.getColor(R.color.warning));
            holder.cardStatus.setCardBackgroundColor(context.getColor(R.color.warning_light));
        }

        // Set click listener cho toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            if (onBookClickListener != null) {
                onBookClickListener.onBookClick(book);
            }
        });

        // Thêm ripple effect
        holder.itemView.setClickable(true);
        holder.itemView.setFocusable(true);
        holder.itemView.setBackground(context.getDrawable(R.drawable.ripple_effect));
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvBookTitle, tvAuthor, tvBookCode, tvStatus;
        CardView cardStatus;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvBookCode = itemView.findViewById(R.id.tvBookCode);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            cardStatus = itemView.findViewById(R.id.cardStatus);
        }
    }
}
