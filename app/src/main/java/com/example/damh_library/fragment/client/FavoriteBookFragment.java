package com.example.damh_library.fragment.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.FavoriteBookAdapter;
import com.example.damh_library.model.response.FavoriteBookResponse;

import java.util.ArrayList;
import java.util.List;

public class FavoriteBookFragment extends Fragment {

    private RecyclerView rvFavoriteBooks;
    private LinearLayout llEmptyState;
    private ImageButton btnBack;
    private FavoriteBookAdapter adapter;
    private List<FavoriteBookResponse> favoriteBooks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_book, container, false);

        initViews(view);
        setupRecyclerView();
        loadFavoriteBooks();

        return view;
    }

    private void initViews(View view) {
        rvFavoriteBooks = view.findViewById(R.id.rvFavoriteBooks);
        llEmptyState = view.findViewById(R.id.llEmptyState);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupRecyclerView() {
        favoriteBooks = new ArrayList<>();
        adapter = new FavoriteBookAdapter(requireContext(), favoriteBooks, new FavoriteBookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(FavoriteBookResponse book) {
                // TODO: Mở chi tiết sách
                Toast.makeText(requireContext(), "Xem chi tiết: " + book.getTenSach(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRemoveFavorite(FavoriteBookResponse book, int position) {
                removeFavorite(book, position);
            }
        });

        rvFavoriteBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavoriteBooks.setAdapter(adapter);
    }

    private void loadFavoriteBooks() {
        // TODO: Gọi API để lấy danh sách sách yêu thích
        // Example: apiService.getFavoriteBooks(userId).enqueue(callback);

        // Dữ liệu mẫu
        favoriteBooks.clear();
        favoriteBooks.add(new FavoriteBookResponse(
                "978-604-2-14559-3",
                "Đắc Nhân Tâm",
                "Dale Carnegie",
                "NXB Tổng Hợp TP.HCM",
                "15/10/2024"
        ));
        favoriteBooks.add(new FavoriteBookResponse(
                "978-604-2-25896-4",
                "Nhà Giả Kim",
                "Paulo Coelho",
                "NXB Hội Nhà Văn",
                "20/09/2024"
        ));
        favoriteBooks.add(new FavoriteBookResponse(
                "978-604-2-13658-4",
                "Tuổi Trẻ Đáng Giá Bao Nhiêu",
                "Rosie Nguyễn",
                "NXB Hội Nhà Văn",
                "05/08/2024"
        ));

        updateUI();
    }

    private void updateUI() {
        if (favoriteBooks.isEmpty()) {
            rvFavoriteBooks.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvFavoriteBooks.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void removeFavorite(FavoriteBookResponse book, int position) {
        // TODO: Gọi API để xóa khỏi danh sách yêu thích
        // Example: apiService.removeFavorite(userId, book.getIsbn()).enqueue(callback);

        favoriteBooks.remove(position);
        adapter.notifyItemRemoved(position);

        Toast.makeText(requireContext(), "Đã xóa \"" + book.getTenSach() + "\" khỏi yêu thích", Toast.LENGTH_SHORT).show();

        updateUI();
    }
}