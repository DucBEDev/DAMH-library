package com.example.damh_library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.model.PdfChatModels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfChatAdapter extends RecyclerView.Adapter<PdfChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    private List<PdfChatModels.ChatMessage> messages;
    private OnCitationClickListener citationClickListener;

    public interface OnCitationClickListener {
        void onCitationClick(int page);
    }

    public PdfChatAdapter(List<PdfChatModels.ChatMessage> messages,
                          OnCitationClickListener listener) {
        this.messages = messages;
        this.citationClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == VIEW_TYPE_USER ?
                R.layout.item_chat_user : R.layout.item_chat_bot;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        PdfChatModels.ChatMessage message = messages.get(position);

        // Set message text
        holder.tvMessage.setText(message.getMessage());

        // Format and set timestamp
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.tvTimestamp.setText(timeFormat.format(new Date(message.getTimestamp())));

        // Handle citations (only for bot messages)
        if (!message.isUser() && holder.llCitations != null) {
            if (message.getCitations() != null && !message.getCitations().isEmpty()) {
                holder.llCitations.setVisibility(View.VISIBLE);
                holder.llCitations.removeAllViews();

                // Add header
                TextView citationHeader = new TextView(holder.itemView.getContext());
                citationHeader.setText("📚 Nguồn tham khảo:");
                citationHeader.setTextSize(12);
                citationHeader.setTextColor(0xFF757575);
                citationHeader.setPadding(0, 8, 0, 8);
                holder.llCitations.addView(citationHeader);

                // Add each citation
                for (PdfChatModels.Citation citation : message.getCitations()) {
                    View citationView = LayoutInflater.from(holder.itemView.getContext())
                            .inflate(R.layout.item_citation, holder.llCitations, false);

                    TextView tvCitation = citationView.findViewById(R.id.tvCitationText);

                    // Format citation text
                    String citationText = String.format(Locale.getDefault(),
                            "[%s] Trang %d",
                            citation.getRef_id() != null ? citation.getRef_id() : "ref",
                            citation.getPage()
                    );

                    // Add preview if available
                    if (citation.getText_preview() != null && !citation.getText_preview().isEmpty()) {
                        String preview = citation.getText_preview();
                        if (preview.length() > 80) {
                            preview = preview.substring(0, 80) + "...";
                        }
                        citationText += ": \"" + preview + "\"";
                    }

                    tvCitation.setText(citationText);

                    // Set click listener to navigate to page
                    citationView.setOnClickListener(v -> {
                        if (citationClickListener != null) {
                            citationClickListener.onCitationClick(citation.getPage());
                        }
                    });

                    holder.llCitations.addView(citationView);
                }
            } else {
                holder.llCitations.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(PdfChatModels.ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void updateLastMessage(PdfChatModels.ChatMessage message) {
        if (!messages.isEmpty()) {
            messages.set(messages.size() - 1, message);
            notifyItemChanged(messages.size() - 1);
        }
    }

    public void clear() {
        int size = messages.size();
        messages.clear();
        notifyItemRangeRemoved(0, size);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTimestamp;
        LinearLayout llCitations;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            llCitations = itemView.findViewById(R.id.llCitations);
        }
    }
}