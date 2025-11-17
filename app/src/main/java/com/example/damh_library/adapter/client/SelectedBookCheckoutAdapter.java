package com.example.damh_library.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.model.response.BookCartResponse;
import java.util.List;

public class SelectedBookCheckoutAdapter extends RecyclerView.Adapter<SelectedBookCheckoutAdapter.ViewHolder> {

    private Context context;
    private List<BookCartResponse> books;

    public SelectedBookCheckoutAdapter(Context context, List<BookCartResponse> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_book_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookCartResponse book = books.get(position);

        holder.tvBookTitle.setText(book.getTitle() != null ? book.getTitle() : "Không có tiêu đề");
        holder.tvBookAuthor.setText("Tác giả: " + (book.getAuthor() != null ? book.getAuthor() : "Chưa rõ"));
        holder.tvMaSach.setText("Mã sách: " + (book.getMaSach() != null ? book.getMaSach() : "N/A"));

        int available = book.getSoLuongKhaDung() != null ? book.getSoLuongKhaDung() : 0;
        holder.tvAvailable.setText("Còn " + available + " cuốn");
        if (available == 0) {
            holder.tvAvailable.setText("Hết sách");
            holder.tvAvailable.setTextColor(context.getColor(R.color.error));
        } else {
            holder.tvAvailable.setTextColor(context.getColor(R.color.teal_700));
        }

        // Load ảnh bìa
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(holder.ivBookCover);
        } else {
            holder.ivBookCover.setImageResource(R.drawable.ic_book_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvBookTitle, tvBookAuthor, tvMaSach, tvAvailable;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvMaSach = itemView.findViewById(R.id.tvMaSach);
            tvAvailable = itemView.findViewById(R.id.tvAvailable);
        }
    }
}