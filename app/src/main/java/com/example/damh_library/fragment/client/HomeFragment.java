package com.example.damh_library.fragment.client;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.MostBorrowBookAdapter;
import com.example.damh_library.adapter.client.BookListAdapter;
import com.example.damh_library.adapter.client.CategoryAdapter;
import com.example.damh_library.model.response.MostBorrowBookResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.network.client.DauSachApiService;
import com.example.damh_library.network.client.BookTypeApiService;
import com.example.damh_library.model.response.TypeBorrowResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {

     private RecyclerView rvMostBorrowed, rvMostCategory, rvMostQuantity;
     private TextView tvUserName;

     @Nullable
     @Override
     public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_home, container, false);

         tvUserName = view.findViewById(R.id.tvUserName);


         rvMostBorrowed = view.findViewById(R.id.rvMostBorrowed);
         rvMostCategory = view.findViewById(R.id.rvMostCategory);
         rvMostQuantity = view.findViewById(R.id.rvMostQuantity);

         setupRecyclerViews();
         return view;
     }

     @Override
     public void onResume() {
         super.onResume();
         String username = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                 .getString("key_username", "");
         if (tvUserName != null) {
             if (username != null && !username.isEmpty()) tvUserName.setText(username);
             else tvUserName.setText(getString(R.string.app_name));
         }
     }

     private void setupRecyclerViews() {
         // Adapters setup for categories and quantity
         rvMostCategory.setLayoutManager(
                 new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
         rvMostCategory.setHasFixedSize(true);
         // show empty adapter while loading
         rvMostCategory.setAdapter(new CategoryAdapter(new ArrayList<>()));
         fetchTopCategories(5);

         rvMostQuantity.setLayoutManager(
                 new LinearLayoutManager(getContext()));
         rvMostQuantity.setHasFixedSize(true);
         // show empty adapter while loading
         rvMostQuantity.setAdapter(new BookListAdapter(new ArrayList<>()));
         fetchMostQuantity(5);

         // Setup for most borrowed: layout manager now, data will come from API
         rvMostBorrowed.setLayoutManager(
                 new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
         rvMostBorrowed.setHasFixedSize(true);
        // show empty adapter while loading
         rvMostBorrowed.setAdapter(new MostBorrowBookAdapter(new ArrayList<>()));
         fetchMostBorrowed(5);
     }

     private void fetchTopCategories(int limit) {
         BookTypeApiService service = ApiClient.getClient().create(BookTypeApiService.class);
         Call<ResponseModel<TypeBorrowResponse>> call = service.getMostBorrowedTypes(limit);
         call.enqueue(new Callback<ResponseModel<TypeBorrowResponse>>() {
             @Override
             public void onResponse(Call<ResponseModel<TypeBorrowResponse>> call, Response<ResponseModel<TypeBorrowResponse>> response) {
                 if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                     List<TypeBorrowResponse> types = response.body().getData();
                     List<TypeBorrowResponse> categories = new ArrayList<>();
                     for (TypeBorrowResponse t : types) {
                         categories.add(new TypeBorrowResponse(t.getType(), t.getBorrowCount()));
                     }
                     rvMostCategory.setAdapter(new CategoryAdapter(categories));
                 } else {
                     rvMostCategory.setAdapter(new CategoryAdapter(new ArrayList<>()));
                     if (response.body() != null) {
                         Toasty.error(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                     } else {
                         Toasty.error(requireContext(), "Lỗi khi tải danh sách thể loại", Toast.LENGTH_SHORT).show();
                     }
                 }
             }

             @Override
             public void onFailure(Call<ResponseModel<TypeBorrowResponse>> call, Throwable t) {
                 rvMostCategory.setAdapter(new CategoryAdapter(new ArrayList<>()));
                 Toasty.error(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });
     }

     private void fetchMostBorrowed(int limit) {
         DauSachApiService mostBorrowBookApiService = ApiClient.getClient().create(DauSachApiService.class);
         Call<ResponseModel<MostBorrowBookResponse>> call = mostBorrowBookApiService.getMostBorrowed(limit);
         call.enqueue(new Callback<ResponseModel<MostBorrowBookResponse>>() {
             @Override
             public void onResponse(Call<ResponseModel<MostBorrowBookResponse>> call, Response<ResponseModel<MostBorrowBookResponse>> response) {
                 if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                     List<MostBorrowBookResponse> mostBorrowedMostBorrowBookResponses = response.body().getData();

                     // Log received image URLs for debugging
//                    for (Book b : mostBorrowedBooks) {
//                        Log.d(TAG, "Book received: title=" + b.getTitle() + ", imagePath=" + b.getImagePath());
//
//                        final String img = b.getImagePath();
//                        if (img != null && !img.isEmpty()) {
//                            // Preload each image and log success/failure
//                            Glide.with(requireContext())
//                                    .asDrawable()
//                                    .load(img)
//                                    .listener(new RequestListener<Drawable>() {
//                                        @Override
//                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                            Log.w(TAG, "Glide preload failed for: " + img + " -> " + (e != null ? e.getMessage() : "unknown"));
//                                            return false;
//                                        }
//
//                                        @Override
//                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                            Log.d(TAG, "Glide preload success for: " + img);
//                                            return false;
//                                        }
//                                    })
//                                    .preload();
//                        }
//                    }

                     // Ensure adapter is set on main thread (Retrofit's callback already runs on main)
                     rvMostBorrowed.setAdapter(new MostBorrowBookAdapter(mostBorrowedMostBorrowBookResponses));
                 } else {
                     Toasty.error(requireContext(), response.body() != null ? response.body().getMessage() : "Lỗi khi tải sách", Toast.LENGTH_SHORT).show();
                     rvMostBorrowed.setAdapter(new MostBorrowBookAdapter(new ArrayList<>()));
                 }
             }

             @Override
             public void onFailure(Call<ResponseModel<MostBorrowBookResponse>> call, Throwable t) {
                 Toasty.error(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                 rvMostBorrowed.setAdapter(new MostBorrowBookAdapter(new ArrayList<>()));
             }
         });
     }

     private void fetchMostQuantity(int limit) {
         DauSachApiService service = ApiClient.getClient().create(DauSachApiService.class);
         Call<ResponseModel<MostBorrowBookResponse>> call = service.getMostQuantity(limit);
         call.enqueue(new Callback<ResponseModel<MostBorrowBookResponse>>() {
             @Override
             public void onResponse(Call<ResponseModel<MostBorrowBookResponse>> call, Response<ResponseModel<MostBorrowBookResponse>> response) {
                 if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                     List<MostBorrowBookResponse> books = response.body().getData();
                     rvMostQuantity.setAdapter(new BookListAdapter(books));
                 } else {
                     rvMostQuantity.setAdapter(new BookListAdapter(new ArrayList<>()));
                     if (response.body() != null) {
                         Toasty.error(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                     } else {
                         Toasty.error(requireContext(), "Lỗi khi tải danh sách sách nhiều nhất", Toast.LENGTH_SHORT).show();
                     }
                 }
             }

             @Override
             public void onFailure(Call<ResponseModel<MostBorrowBookResponse>> call, Throwable t) {
                 rvMostQuantity.setAdapter(new BookListAdapter(new ArrayList<>()));
                 Toasty.error(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });
     }
 }
