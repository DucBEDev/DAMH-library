package com.example.damh_library.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.example.damh_library.R;
import com.example.damh_library.model.response.BookCartResponse;

import java.util.ArrayList;
import java.util.List;

public class BookCartAdapter extends RecyclerView.Adapter<BookCartAdapter.CartViewHolder> {

    private Context context;
    private List<BookCartResponse> cartBooks;
    private List<BookCartResponse> selectedBooks;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onBookClick(BookCartResponse book);
        void onRemoveFromCart(BookCartResponse book, int position);
        void onSelectionChanged(List<BookCartResponse> selectedBooks);
    }

    public BookCartAdapter(Context context, List<BookCartResponse> cartBooks, OnCartItemListener listener) {
        this.context = context;
        this.cartBooks = cartBooks;
        this.selectedBooks = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        BookCartResponse book = cartBooks.get(position);

        // Set book information
        holder.tvBookTitle.setText(book.getTitle() != null ? book.getTitle() : "Không có tên");
        holder.tvAuthor.setText(book.getAuthor() != null ? book.getAuthor() : "Chưa rõ tác giả");
        holder.tvPublisher.setText(book.getPublisher() != null ? book.getPublisher() : "Chưa rõ NXB");
        holder.tvISBN.setText("ISBN: " + (book.getIsbn() != null ? book.getIsbn() : "N/A"));

        // Available count
        int availableCount = book.getSoLuongKhaDung() != null ? book.getSoLuongKhaDung() : 0;
        holder.tvAvailableCount.setText("Còn " + availableCount + " cuốn");

        // Update available badge color
        if (availableCount > 0) {
            holder.tvAvailableCount.setTextColor(context.getColor(R.color.success));
        } else {
            holder.tvAvailableCount.setTextColor(context.getColor(R.color.error));
            holder.tvAvailableCount.setText("Hết sách");
        }

        // Checkbox state
        holder.cbSelectBook.setChecked(selectedBooks.contains(book));
        holder.cbSelectBook.setEnabled(availableCount > 0);

        // Load book cover image
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivBookCover);
        } else {
            holder.ivBookCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        // Checkbox change listener
        holder.cbSelectBook.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedBooks.contains(book)) {
                    selectedBooks.add(book);
                }
            } else {
                selectedBooks.remove(book);
            }
            if (listener != null) {
                listener.onSelectionChanged(selectedBooks);
            }
        });

        // Click on card to toggle selection
        holder.cardBook.setOnClickListener(v -> {
            if (availableCount > 0) {
                holder.cbSelectBook.setChecked(!holder.cbSelectBook.isChecked());
            }
        });

        // Remove button
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveFromCart(book, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartBooks.size();
    }

    // Public methods for selection management
    public void selectAll() {
        selectedBooks.clear();
        for (BookCartResponse book : cartBooks) {
            if (book.getSoLuongKhaDung() != null && book.getSoLuongKhaDung() > 0) {
                selectedBooks.add(book);
            }
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(selectedBooks);
        }
    }

    public void deselectAll() {
        selectedBooks.clear();
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(selectedBooks);
        }
    }

    public List<BookCartResponse> getSelectedBooks() {
        return new ArrayList<>(selectedBooks);
    }

    public int getSelectedCount() {
        return selectedBooks.size();
    }

    public boolean isAllSelected() {
        int availableCount = 0;
        for (BookCartResponse book : cartBooks) {
            if (book.getSoLuongKhaDung() != null && book.getSoLuongKhaDung() > 0) {
                availableCount++;
            }
        }
        return availableCount > 0 && selectedBooks.size() == availableCount;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardBook;
        CheckBox cbSelectBook;
        ImageView ivBookCover;
        TextView tvBookTitle, tvAuthor, tvPublisher, tvISBN, tvAvailableCount;
        ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cardBook = itemView.findViewById(R.id.cardBook);
            cbSelectBook = itemView.findViewById(R.id.cbSelectBook);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPublisher = itemView.findViewById(R.id.tvPublisher);
            tvISBN = itemView.findViewById(R.id.tvISBN);
            tvAvailableCount = itemView.findViewById(R.id.tvAvailableCount);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}