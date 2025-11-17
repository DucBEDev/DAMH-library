package com.example.damh_library.fragment.client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.damh_library.R;
import com.example.damh_library.adapter.client.BookListAdapter;
import com.example.damh_library.adapter.client.CheckoutHistoryAdapter;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.response.CheckoutHistoryResponse;
import com.example.damh_library.model.response.MostBorrowBookResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.CheckoutSlipApiService;
import com.example.damh_library.network.client.DauSachApiService;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutHistoryListFragment extends Fragment {

    private RecyclerView recyclerView;
    private CheckoutHistoryAdapter adapter;

    public CheckoutHistoryListFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout_history_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.rvCheckoutHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CheckoutHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadCheckoutHistory();
    }

    // Replace this with actual API/service call using your auth/user id
    private void loadCheckoutHistory() {
        // Example placeholder data
        String userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("key_userId", "5");
//        List<CheckoutHistoryResponse> demo = new ArrayList<>();
//        demo.add(new CheckoutHistoryResponse(1001L, true, "2025-11-10"));
//        demo.add(new CheckoutHistoryResponse(1000L, false, "2025-10-05"));
//        adapter.setItems(demo);

        // TODO: call your service (e.g. CheckoutHistoryApiService) and update adapter.setItems(...)
        CheckoutSlipApiService service = ApiClient.getClient().create(CheckoutSlipApiService.class);
        Call<ResponseModel<CheckoutHistoryResponse>> call = service.getReaderCheckoutHistory(userId);
        call.enqueue(new Callback<ResponseModel<CheckoutHistoryResponse>>() {
            @Override
            public void onResponse(Call<ResponseModel<CheckoutHistoryResponse>> call, Response<ResponseModel<CheckoutHistoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CheckoutHistoryResponse> checkouts = response.body().getData();
                    Log.e("CheckoutHistory", checkouts.toString());
                    recyclerView.setAdapter(new CheckoutHistoryAdapter(checkouts));
                } else {
                    recyclerView.setAdapter(new CheckoutHistoryAdapter(new ArrayList<>()));
                    if (response.body() != null) {
                        Toasty.error(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(requireContext(), "Lỗi khi tải danh sách phiếu mượn", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<CheckoutHistoryResponse>> call, Throwable t) {
                recyclerView.setAdapter(new CheckoutHistoryAdapter(new ArrayList<>()));
                Toasty.error(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
