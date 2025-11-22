package com.example.damh_library.adapter.client;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.fragment.client.BookDetailFragment;
import com.example.damh_library.model.response.MostBorrowBookResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {
    private List<MostBorrowBookResponse> mostBorrowBookResponseList;

    public BookListAdapter(List<MostBorrowBookResponse> mostBorrowBookResponseList) {
        this.mostBorrowBookResponseList = mostBorrowBookResponseList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_list, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        MostBorrowBookResponse mostBorrowBookResponse = mostBorrowBookResponseList.get(position);
        holder.tvBookTitle.setText(mostBorrowBookResponse.getTitle() != null ? mostBorrowBookResponse.getTitle() : "");
        holder.tvBookAuthor.setText("Tác giả: " + (mostBorrowBookResponse.getAuthor() != null ? mostBorrowBookResponse.getAuthor() : "-"));
        holder.tvQuantity.setText("Có sẵn: " + mostBorrowBookResponse.getQuantity() + " cuốn");

        // set type/category if available
        String type = mostBorrowBookResponse.getType();
        if (type == null || type.isEmpty()) type = "";
        holder.tvBookCategory.setText("Thể loại: " + type);

        // load cover image if available
        String imageUrl = mostBorrowBookResponse.getImagePath();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .fitCenter()
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(holder.ivBookCover);
        } else {
            holder.ivBookCover.setImageResource(R.drawable.ic_book);
        }

        // Chuyển đến BookDetailFragment khi click item
        holder.itemView.setOnClickListener(v -> {
            if (holder.itemView.getContext() instanceof AppCompatActivity) {
                String isbn = mostBorrowBookResponse.getIsbn(); // Giả sử có getIsbn() trong model
                Log.e("AAA", isbn);
                BookDetailFragment fragment = BookDetailFragment.newInstance(isbn);
                AppCompatActivity activity = (AppCompatActivity) holder.itemView.getContext();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentClientDashboard, fragment) // Thay bằng container ID của bạn
                        .addToBackStack(null)
                        .commit();
            }
        });

//        holder.btnBorrow.setOnClickListener(v -> {
//            if (holder.itemView.getContext() instanceof AppCompatActivity) {
//                CheckoutSlipFragment fragment = new CheckoutSlipFragment();
//                AppCompatActivity activity = (AppCompatActivity) holder.itemView.getContext();
//                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.fragmentClientDashboard, fragment);
//                ft.addToBackStack(null);
//                ft.commit();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mostBorrowBookResponseList == null ? 0 : mostBorrowBookResponseList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle, tvBookAuthor, tvQuantity, tvBookCategory;
        MaterialButton btnBorrow;
        ImageView ivBookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnBorrow = itemView.findViewById(R.id.btnBorrow);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookCategory = itemView.findViewById(R.id.tvBookCategory);
        }
    }
}