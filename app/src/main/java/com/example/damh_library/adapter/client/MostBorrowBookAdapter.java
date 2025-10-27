package com.example.damh_library.adapter.client;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.damh_library.R;
import com.example.damh_library.model.response.MostBorrowBookResponse;

import java.util.List;

public class MostBorrowBookAdapter extends RecyclerView.Adapter<MostBorrowBookAdapter.BookViewHolder> {
    private final List<MostBorrowBookResponse> mostBorrowBookResponseList;

    public MostBorrowBookAdapter(List<MostBorrowBookResponse> mostBorrowBookResponseList) {
        this.mostBorrowBookResponseList = mostBorrowBookResponseList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_horizontal, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        MostBorrowBookResponse mostBorrowBookResponse = mostBorrowBookResponseList.get(position);

        // Guard against nulls
        String title = mostBorrowBookResponse.getTitle() != null ? mostBorrowBookResponse.getTitle() : holder.itemView.getContext().getString(R.string.book_title_placeholder);
        String author = mostBorrowBookResponse.getAuthor() != null ? mostBorrowBookResponse.getAuthor() : holder.itemView.getContext().getString(R.string.book_author_placeholder);

        holder.tvBookTitle.setText(title);
        holder.tvBookAuthor.setText(author);

        // Use string resource with placeholder for borrow count
        holder.tvBorrowCount.setText(holder.itemView.getContext().getString(R.string.borrow_count_format, mostBorrowBookResponse.getBorrowCount()));

        String imageUrl = mostBorrowBookResponse.getImagePath();

        // ensure ImageView uses FIT_CENTER so full image is visible
        holder.ivBookCover.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Log.d("BookImage", "Binding position=" + position + " url=" + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .fitCenter()
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.w("BookImage", "Failed to load image: " + imageUrl + " -> " + (e != null ? e.getMessage() : "unknown"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("BookImage", "Loaded image: " + imageUrl);
                            return false;
                        }
                    })
                    .into(holder.ivBookCover);
        } else {
            holder.ivBookCover.setImageResource(R.drawable.ic_book);
        }
    }

    @Override
    public int getItemCount() {
        return mostBorrowBookResponseList == null ? 0 : mostBorrowBookResponseList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle, tvBookAuthor, tvBorrowCount;
        ImageView ivBookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBorrowCount = itemView.findViewById(R.id.tvBorrowCount);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
        }
    }
}
