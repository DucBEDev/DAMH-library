package com.example.damh_library.adapter.client;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.example.damh_library.R;
import com.example.damh_library.model.response.BookCartResponse;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BookCartAdapter extends RecyclerView.Adapter<BookCartAdapter.CartViewHolder> {

    private Context context;
    private List<BookCartResponse> cartBooks;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onBookClick(BookCartResponse book);
        void onRemoveFromCart(BookCartResponse book, int position);
        void onSelectionChanged(List<BookCartResponse> selectedBooks);
    }

    public BookCartAdapter(Context context, List<BookCartResponse> cartBooks, OnCartItemListener listener) {
        this.context = context;
        this.cartBooks = cartBooks;
        this.listener = listener;
    }

    private String normIsbn(String isbn) {
        if (isbn == null) return null;
        return isbn.trim();
    }

    private boolean isBookSelected(BookCartResponse book) {
        return book != null && book.isSelected();
    }

    private void setBookSelected(BookCartResponse book, boolean selected) {
        if (book == null) return;
        book.setSelected(selected);
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

        Integer rawAvailable = book.getSoLuongKhaDung();
        boolean knownAvailability = rawAvailable != null;
        int availableCount = knownAvailability ? rawAvailable : 0; // used only for numeric display

        if (knownAvailability) {
            holder.tvAvailableCount.setText("Còn " + availableCount + " cuốn");
            if (availableCount > 0) {
                holder.tvAvailableCount.setTextColor(context.getColor(R.color.success));
            } else {
                holder.tvAvailableCount.setTextColor(context.getColor(R.color.error));
                holder.tvAvailableCount.setText("Hết sách");
            }
        } else {
            holder.tvAvailableCount.setText("Còn: -");
            holder.tvAvailableCount.setTextColor(context.getColor(R.color.text_secondary));
        }

        // Checkbox state
        holder.cbSelectBook.setOnCheckedChangeListener(null);
        holder.cbSelectBook.setChecked(isBookSelected(book));
        boolean selectable = knownAvailability ? (availableCount > 0) : true;
        holder.cbSelectBook.setEnabled(selectable);

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

        // Checkbox change listener - respond to checked state changes and guard max-3
        final android.widget.CompoundButton.OnCheckedChangeListener[] cbRef = new android.widget.CompoundButton.OnCheckedChangeListener[1];
        cbRef[0] = (buttonView, isChecked) -> {
            int selectedCount = getSelectedCount();
            if (isChecked) {
                if (selectedCount >= 3) {
                    holder.cbSelectBook.setOnCheckedChangeListener(null);
                    holder.cbSelectBook.setChecked(false);
                    holder.cbSelectBook.setOnCheckedChangeListener(cbRef[0]);
                    Toasty.warning(context, "Bạn chỉ được chọn tối đa 3 cuốn để mượn", Toast.LENGTH_SHORT).show();
                    return;
                }
                setBookSelected(book, true);
            } else {
                setBookSelected(book, false);
            }
            if (listener != null) listener.onSelectionChanged(buildSelectedListFromPositions());
        };

        holder.cbSelectBook.setOnCheckedChangeListener(cbRef[0]);

        // Click on card to toggle selection
        holder.cardBook.setOnClickListener(v -> {
            if (selectable) {
                holder.cbSelectBook.performClick();
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
        for (BookCartResponse book : cartBooks) setBookSelected(book, false);
        // select first up to 3 selectable books
        int added = 0;
        for (BookCartResponse book : cartBooks) {
            boolean allow = (book.getSoLuongKhaDung() == null) || (book.getSoLuongKhaDung() > 0);
            if (allow && added < 3) {
                setBookSelected(book, true);
                added++;
            }
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(buildSelectedListFromPositions());
        }
    }

    public void deselectAll() {
        for (BookCartResponse b : cartBooks) if (b != null) b.setSelected(false);
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(new ArrayList<>());
        }
    }

    public List<BookCartResponse> getSelectedBooks() {
        return buildSelectedListFromPositions();
    }

    public int getSelectedCount() {
        // Return number of items currently marked selected (matches visible checked boxes)
        return buildSelectedListFromPositions().size();
    }

    // Count how many items are actually selectable (same logic used for enabling selection)
    public int getSelectableCount() {
        int selectableCount = 0;

        for (int i = 0; i < cartBooks.size(); i++) {
            BookCartResponse book = cartBooks.get(i);
            if (book == null) {
                continue;
            }

            Integer qty = book.getSoLuongKhaDung();
            boolean allow = (qty == null) || (qty > 0);
            if (allow) selectableCount++;
        }
        return selectableCount;
    }

    public boolean isAllSelected() {
        int selectableCount = getSelectableCount();
        if (selectableCount == 0) return false;
        return getSelectedCount() == selectableCount;
    }

    // Call when an item is removed from the adapter's data at index removedIndex
    public void onItemRemoved(int removedIndex) {
        if (removedIndex < 0) return;
        notifyDataSetChanged();
        if (listener != null) listener.onSelectionChanged(buildSelectedListFromPositions());
    }

    // Build selected BookCartResponse list from positions
    private List<BookCartResponse> buildSelectedListFromPositions() {
        List<BookCartResponse> out = new ArrayList<>();
        for (BookCartResponse b : cartBooks) {
            if (b == null) continue;
            if (b.isSelected()) out.add(b);
        }

        if (!out.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (BookCartResponse b : out) sb.append(b.getTitle()).append(" | ");
        }
        return out;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardBook;
        private ImageView ivBookCover;
        private TextView tvBookTitle, tvAuthor, tvPublisher, tvISBN, tvAvailableCount;
        private MaterialCheckBox cbSelectBook;
        private ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBook = itemView.findViewById(R.id.cardBook);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPublisher = itemView.findViewById(R.id.tvPublisher);
            tvISBN = itemView.findViewById(R.id.tvISBN);
            tvAvailableCount = itemView.findViewById(R.id.tvAvailableCount);
            cbSelectBook = itemView.findViewById(R.id.cbSelectBook);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
