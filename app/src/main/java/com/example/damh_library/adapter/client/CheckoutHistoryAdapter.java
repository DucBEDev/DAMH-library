package com.example.damh_library.adapter.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.damh_library.R;
import com.example.damh_library.model.response.CheckoutHistoryResponse;
import java.util.List;

public class CheckoutHistoryAdapter extends RecyclerView.Adapter<CheckoutHistoryAdapter.ViewHolder> {

    private List<CheckoutHistoryResponse> items;

    public CheckoutHistoryAdapter(List<CheckoutHistoryResponse> items) {
        this.items = items;
    }

    public void setItems(List<CheckoutHistoryResponse> items) {
        this.items = items;
        notifyDataSetChanged();
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
        holder.tvSlipId.setText(String.valueOf(item.getMaphieu()));
        holder.tvForm.setText(item.isHinhThuc() ? "Mang về" : "Tại chỗ");
        holder.tvDate.setText(item.getNgayMuon());
        // You can add click listeners on holder.card if needed
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvSlipId, tvForm, tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardItem);
            tvSlipId = itemView.findViewById(R.id.tvSlipId);
            tvForm = itemView.findViewById(R.id.tvForm);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
