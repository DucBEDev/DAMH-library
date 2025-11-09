package com.example.damh_library.fragment.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damh_library.R;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.response.ReaderCardResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.ReaderApiService;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderCardFragment extends Fragment {

    private ImageButton btnBack;
    private ImageView ivReaderAvatar;
    private TextView tvReaderNameCard, tvReaderCode, tvIssueDate, tvExpiryDate, tvStatus, tvDaysRemaining, tvEmail, tvPhone;
    private MaterialButton btnRenewCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reader_card, container, false);

        bindViews(view);
        loadSavedUserInfo();
        fetchCardInfo();
        setupListeners();

        return view;
    }

    private void bindViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        ivReaderAvatar = view.findViewById(R.id.ivReaderAvatar);
        tvReaderNameCard = view.findViewById(R.id.tvReaderNameCard);
        tvReaderCode = view.findViewById(R.id.tvReaderCode);
        tvIssueDate = view.findViewById(R.id.tvIssueDate);
        tvExpiryDate = view.findViewById(R.id.tvExpiryDate);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvDaysRemaining = view.findViewById(R.id.tvDaysRemaining);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        btnRenewCard = view.findViewById(R.id.btnRenewCard);
    }

    private void loadSavedUserInfo() {
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("key_username", "Độc giả");
        String email = prefs.getString("key_userEmail", "");
        String phone = prefs.getString("key_userPhone", prefs.getString("key_userPhone", ""));

        tvReaderNameCard.setText(username);
        tvEmail.setText(email != null && !email.isEmpty() ? email : "Chưa cập nhật");
        tvPhone.setText(phone != null && !phone.isEmpty() ? phone : "Chưa cập nhật");

        // default avatar
        ivReaderAvatar.setImageResource(R.drawable.ic_user);
    }

    private void fetchCardInfo() {
        String userId = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("key_userId", "2");

        ReaderApiService service = ApiClient.getClient().create(ReaderApiService.class);
        Call<ResponseSingleModel<ReaderCardResponse>> call = service.getCardInfo(userId);
        call.enqueue(new Callback<ResponseSingleModel<ReaderCardResponse>>() {
            @Override
            public void onResponse(@NotNull Call<ResponseSingleModel<ReaderCardResponse>> call, @NotNull Response<ResponseSingleModel<ReaderCardResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ReaderCardResponse card = response.body().getData();

                    if (card != null) {
                        updateUiWithCard(card);
                    } else {
                        Toasty.error(requireContext(), "Dữ liệu bị lỗi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String msg = "Lấy thông tin thẻ thất bại";
                    if (response.body() != null && response.body().getMessage() != null) msg = response.body().getMessage();
                    Toasty.error(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseSingleModel<ReaderCardResponse>> call, @NotNull Throwable t) {
                Log.w("ReaderCard", "fetchCardInfo failed: " + t.getMessage());
                Toasty.error(requireContext(), "Lỗi mạng khi lấy thẻ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUiWithCard(ReaderCardResponse card) {
        // mã độc giả
        String code = card.getMaDG();
        if (code == null) code = "";
        tvReaderCode.setText("DG-" + code);

        // dates
        String issueIso = card.getNgayLamThe();
        String expiryIso = card.getNgayHetHan();

        String issueDisplay = formatIsoToDisplay(issueIso);
        String expiryDisplay = formatIsoToDisplay(expiryIso);
        tvIssueDate.setText(issueDisplay);
        tvExpiryDate.setText(expiryDisplay);

        // compute days remaining
        long days = computeDaysRemaining(expiryIso);
        if (days >= 0) {
            tvDaysRemaining.setText(days + " ngày");
            tvStatus.setText("Còn hiệu lực");
            tvStatus.setTextColor(getResources().getColor(R.color.teal_700));
        } else {
            tvDaysRemaining.setText("0 ngày");
            tvStatus.setText("Hết hạn");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private String formatIsoToDisplay(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date d = input.parse(iso);
            if (d == null) return iso;
            return out.format(d);
        } catch (ParseException e) {
            Log.w("ReaderCard", "formatIsoToDisplay failed: " + e.getMessage());
            return iso;
        }
    }

    private long computeDaysRemaining(String expiryIso) {
        if (expiryIso == null || expiryIso.isEmpty()) return -1;
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date expiry = input.parse(expiryIso);
            if (expiry == null) return -1;
            long diff = expiry.getTime() - new Date().getTime();
            return TimeUnit.MILLISECONDS.toDays(diff);
        } catch (Exception e) {
            Log.w("ReaderCard", "computeDaysRemaining failed: " + e.getMessage());
            return -1;
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnRenewCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng gia hạn thẻ đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }
}