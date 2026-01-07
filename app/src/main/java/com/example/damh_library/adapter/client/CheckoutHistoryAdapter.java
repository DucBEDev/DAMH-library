package com.example.damh_library.adapter.client;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.damh_library.R;
import com.example.damh_library.model.response.BookInCheckoutResponse;
import com.example.damh_library.model.response.CheckoutHistoryResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutHistoryAdapter extends RecyclerView.Adapter<CheckoutHistoryAdapter.ViewHolder> {

    private List<CheckoutHistoryResponse> items;
    private OnBookClickListener onBookClickListener;

    public interface OnBookClickListener {
        void onBookClick(BookInCheckoutResponse book);
    }

    public CheckoutHistoryAdapter(List<CheckoutHistoryResponse> items) {
        this.items = items;
    }

    public CheckoutHistoryAdapter(List<CheckoutHistoryResponse> items, OnBookClickListener listener) {
        this.items = items;
        this.onBookClickListener = listener;
    }

    public void setItems(List<CheckoutHistoryResponse> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnBookClickListener(OnBookClickListener listener) {
        this.onBookClickListener = listener;
    }

    @NonNull
    @Override
    public CheckoutHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutHistoryAdapter.ViewHolder holder, int position) {
        CheckoutHistoryResponse item = items.get(position);
        
        // Hiển thị mã phiếu
        holder.tvSlipId.setText("Mã phiếu: " + item.getMaPhieu());
        
        // Hiển thị hình thức mượn
        if(item.isHinhThuc()==null)
        {
            holder.tvForm.setText("Hình thức: Trực tuyến");
        }
        else if (item.isHinhThuc()==true)
        {
            holder.tvForm.setText("Hình thức: Mang về");
        }
        else if (item.isHinhThuc()==false)
        {
            holder.tvForm.setText("Hình thức: Tại chỗ");
        }
//        holder.tvForm.setText("Hình thức: " + (item.isHinhThuc() ? "Mang về" : "Tại chỗ"));
        
        // Format và hiển thị ngày mượn
        String formattedDate = formatDate(item.getNgayMuon());
        holder.tvDate.setText(formattedDate);
        
        // Hiển thị số lượng sách
        holder.tvBookCount.setText(item.getSoLuongSach() + " cuốn sách");
        
        // Setup expand/collapse functionality
        List<BookInCheckoutResponse> books = item.getDanhSachSach();
        boolean hasBooks = books != null && !books.isEmpty();
        
        if (hasBooks) {
            holder.layoutBookInfo.setVisibility(View.VISIBLE);
            holder.btnExpandCollapse.setVisibility(View.VISIBLE);
            
            // Setup book list adapter with click listener
            BookInCheckoutAdapter bookAdapter = new BookInCheckoutAdapter(
                holder.itemView.getContext(), 
                books,
                book -> {
                    // Truyền click event lên fragment
                    if (onBookClickListener != null) {
                        onBookClickListener.onBookClick(book);
                    }
                }
            );
            
            holder.rvBookList.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.rvBookList.setAdapter(bookAdapter);
            
            // Click listener for expand/collapse
            holder.layoutBookInfo.setOnClickListener(v -> {
                boolean isExpanded = holder.layoutExpandable.getVisibility() == View.VISIBLE;
                
                if (isExpanded) {
                    holder.layoutExpandable.setVisibility(View.GONE);
                    holder.btnExpandCollapse.setImageResource(R.drawable.ic_arrow_down);
                } else {
                    holder.layoutExpandable.setVisibility(View.VISIBLE);
                    holder.btnExpandCollapse.setImageResource(R.drawable.ic_arrow_up);
                }
            });
        } else {
            holder.layoutBookInfo.setVisibility(View.GONE);
            holder.layoutExpandable.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Format ngày từ ISO string sang dd/MM/yyyy
    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(isoDate);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            Log.w("CheckoutAdapter", "Error parsing date: " + isoDate + " -> " + e.getMessage());
        }
        return isoDate; // Fallback to original string
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvSlipId, tvForm, tvDate, tvBookCount;
        LinearLayout layoutBookInfo, layoutExpandable;
        ImageView btnExpandCollapse;
        RecyclerView rvBookList;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardItem);
            tvSlipId = itemView.findViewById(R.id.tvSlipId);
            tvForm = itemView.findViewById(R.id.tvForm);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBookCount = itemView.findViewById(R.id.tvBookCount);
            layoutBookInfo = itemView.findViewById(R.id.layoutBookInfo);
            layoutExpandable = itemView.findViewById(R.id.layoutExpandable);
            btnExpandCollapse = itemView.findViewById(R.id.btnExpandCollapse);
            rvBookList = itemView.findViewById(R.id.rvBookList);
        }
    }
}
