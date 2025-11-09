package com.example.damh_library.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.model.response.FavoriteBookResponse;

import java.util.List;

public class FavoriteBookAdapter extends RecyclerView.Adapter<FavoriteBookAdapter.BookViewHolder> {

    private Context context;
    private List<FavoriteBookResponse> books;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(FavoriteBookResponse book);
        void onRemoveFavorite(FavoriteBookResponse book, int position);
    }

    public FavoriteBookAdapter(Context context, List<FavoriteBookResponse> books, OnBookClickListener listener) {
        this.context = context;
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        FavoriteBookResponse book = books.get(position);

        holder.tvBookTitle.setText(book.getTenSach());
        holder.tvAuthor.setText(book.getTacGia());
        holder.tvPublisher.setText(book.getNxb());
        holder.tvISBN.setText(book.getIsbn());
        holder.tvDateAdded.setText(book.getNgayThem());

        // TODO: Load ảnh bìa sách nếu có URL
        // Glide.with(context).load(book.getImageUrl()).into(holder.ivBookCover);

        // Click vào card để xem chi tiết
        holder.cardBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });

        // Click nút xóa yêu thích
        holder.btnRemoveFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveFavorite(book, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        CardView cardBook;
        ImageView ivBookCover;
        TextView tvBookTitle, tvAuthor, tvPublisher, tvISBN, tvDateAdded;
        ImageButton btnRemoveFavorite;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            cardBook = itemView.findViewById(R.id.cardBook);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPublisher = itemView.findViewById(R.id.tvPublisher);
            tvISBN = itemView.findViewById(R.id.tvISBN);
            tvDateAdded = itemView.findViewById(R.id.tvDateAdded);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
        }
    }
}
