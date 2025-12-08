package com.example.damh_library.fragment.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.adapter.client.SubBookAdapter;
import com.example.damh_library.model.ResponseModel;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.BookCartRequest;
import com.example.damh_library.model.response.BookCartResponse;
import com.example.damh_library.model.response.BookDetailResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.DauSachApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailFragment extends Fragment {

    // Views
    private ImageView ivBookCoverDetail;
    private TextView tvIsbn, tvPublishDate, tvEdition, tvPageCount, tvBookSize, tvPrice, tvAuthorDetail, tvPublisherDetail, tvCategory, tvLanguage;
    private RecyclerView rvSubBooks;
    private MaterialButton btnReadOnline;

    private DauSachApiService apiService;
    private String isbn; // ISBN từ arguments

    public BookDetailFragment() {}

    // Factory method để truyền ISBN
    public static BookDetailFragment newInstance(String isbn) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString("isbn", isbn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isbn = getArguments().getString("isbn");
        }
        apiService = ApiClient.getClient().create(DauSachApiService.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        // Ánh xạ views
        ivBookCoverDetail = view.findViewById(R.id.ivBookCoverDetail);
        tvIsbn = view.findViewById(R.id.tvIsbn);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvLanguage = view.findViewById(R.id.tvLanguage);
        tvPublishDate = view.findViewById(R.id.tvPublishDate);
        tvEdition = view.findViewById(R.id.tvEdition);
        tvPageCount = view.findViewById(R.id.tvPageCount);
        tvBookSize = view.findViewById(R.id.tvBookSize);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvAuthorDetail = view.findViewById(R.id.tvAuthorDetail);
        tvPublisherDetail = view.findViewById(R.id.tvPublisherDetail);
        rvSubBooks = view.findViewById(R.id.rvSubBooks);
        btnReadOnline = view.findViewById(R.id.btnReadOnline);

        // Gọi API để lấy chi tiết
        fetchBookDetail();

        btnReadOnline.setOnClickListener(v->
        {
            isEbookStillBorrowed();
        });

        return view;
    }

    private void fetchBookDetail() {
        if (isbn == null || isbn.trim().isEmpty()) {
            Toasty.error(requireContext(), "Không có ISBN sách!", Toasty.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseSingleModel<BookDetailResponse>> call = apiService.getBookDetail(isbn.trim());
        call.enqueue(new Callback<ResponseSingleModel<BookDetailResponse>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<BookDetailResponse>> call,
                                   Response<ResponseSingleModel<BookDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    BookDetailResponse bookDetail = (BookDetailResponse) response.body().getData(); // <-- Đây là object, không phải list!
                    Log.e("AAA", "Lấy thành công: " + bookDetail.getTitle());
                    bindBookData(bookDetail);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Không có dữ liệu";
                    Toasty.error(requireContext(), "Lỗi: " + msg, Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<BookDetailResponse>> call, Throwable t) {
                Log.e("AAA", "Lỗi mạng: " + t.getMessage());
                Toasty.error(requireContext(), "Không kết nối được server!", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void bindBookData(BookDetailResponse bookDetail) {
        // Load cover
        if (bookDetail.getImagePath() != null && !bookDetail.getImagePath().isEmpty()) {
            Glide.with(requireContext())
                    .load(bookDetail.getImagePath())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(ivBookCoverDetail);
        } else {
            ivBookCoverDetail.setImageResource(R.drawable.ic_book_placeholder);
        }

        tvIsbn.setText("ISBN: " + (bookDetail.getIsbn() != null ? bookDetail.getIsbn() : "Không rõ"));

        // Badges
        tvCategory.setText("Thể loại: " + (bookDetail.getType() != null ? bookDetail.getType() : "Không rõ"));
        tvLanguage.setText("Ngôn ngữ: " + (bookDetail.getLanguage() != null ? bookDetail.getLanguage() : "Không rõ"));

        // Mô tả
        tvPublishDate.setText("Ngày xuất bản: " + (bookDetail.getPublishDate() != null ? bookDetail.getPublishDate() : "Không rõ"));
        tvEdition.setText("Lần xuất bản: " + (bookDetail.getEdition() != null ? bookDetail.getEdition() : "Không rõ"));
        tvPageCount.setText("Số trang: " + (bookDetail.getNumberOfPages() != null ? bookDetail.getNumberOfPages() : "Không rõ"));
        tvBookSize.setText("Khổ sách: " + "Không rõ"); // Không có dữ liệu, giữ "Không rõ"
        tvPrice.setText("Giá: " + (bookDetail.getPrice() != null ? bookDetail.getPrice() + " VNĐ" : "Không rõ"));
        tvAuthorDetail.setText("Tác giả: " + (bookDetail.getAuthor() != null ? bookDetail.getAuthor() : "Không rõ"));
        tvPublisherDetail.setText("Nhà xuất bản: " + (bookDetail.getPublisher() != null ? bookDetail.getPublisher() : "Không rõ"));

        Log.e("AAA", bookDetail.getBookCopies().toString());

        // Danh sách bookCopies
        SubBookAdapter subAdapter = new SubBookAdapter(requireContext(), bookDetail.getBookCopies());
        rvSubBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSubBooks.setAdapter(subAdapter);
    }

    private void isEbookStillBorrowed() {
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        long maDG = Long.parseLong(prefs.getString("key_userId", "0"));

        String maSach = tvIsbn.getText().toString().replace("ISBN: ", "").trim() + "-ON";
        Log.e("AAA", maSach);

        BookCartRequest request = new BookCartRequest( maDG, maSach);
        DauSachApiService service = ApiClient.getClient().create(DauSachApiService.class);
        Call<ResponseSingleModel<Boolean>> call = service.isEbookStillBorrowed(maDG, maSach);
        call.enqueue(new Callback<ResponseSingleModel<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<Boolean>> call, Response<ResponseSingleModel<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Boolean isStillBorrowed = response.body().getData(); // Đây là Boolean (có thể null)

                    if (Boolean.TRUE.equals(isStillBorrowed)) {
                        // Sách vẫn đang được mượn → không cho đọc lại
                        getBookUrl(tvIsbn.getText().toString().replace("ISBN: ", "").trim());
                    } else {
                        // Cho phép đọc ebook

                        showBookNotOwnedDialog();
                    }

                } else {
                    // Lỗi HTTP (400, 500, v.v.) → đọc errorBody
                    String errorMessage = "Lỗi không xác định";

                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            // Parse JSON lỗi thủ công (vì ResponseModel<Void> không match)
                            if (errorJson.contains("message")) {
                                // Dùng regex đơn giản hoặc Gson
                                errorMessage = errorJson.split("\"message\":\"")[1].split("\"")[0];
                            } else {
                                errorMessage = errorJson;
                            }
                        } catch (Exception e) {
                            errorMessage = "Lỗi server";
                        }
                    }

                    Toasty.warning(requireContext(), errorMessage, Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<Boolean>> call, Throwable t) {
                Toasty.error(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void getBookUrl(String isbn) {
        Log.e("AAA", isbn);
        DauSachApiService service = ApiClient.getClient().create(DauSachApiService.class);
        Call<ResponseSingleModel<String>> call = service.getBookUrl(isbn);
        call.enqueue(new Callback<ResponseSingleModel<String>>() {
            @Override
            public void onResponse(Call<ResponseSingleModel<String>> call, Response<ResponseSingleModel<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String bookUrl = response.body().getData();
                    if(bookUrl!=null)
                    {
                        openEbookReader(bookUrl);
                    }
                    else
                    {
                        Toasty.error(requireContext(), "Lỗi khi tải sách" , Toasty.LENGTH_LONG).show();
                    }
                } else {
                    // Lỗi HTTP (400, 500, v.v.) → đọc errorBody
                    String errorMessage = "Lỗi không xác định";

                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            // Parse JSON lỗi thủ công (vì ResponseModel<Void> không match)
                            if (errorJson.contains("message")) {
                                // Dùng regex đơn giản hoặc Gson
                                errorMessage = errorJson.split("\"message\":\"")[1].split("\"")[0];
                            } else {
                                errorMessage = errorJson;
                            }
                        } catch (Exception e) {
                            errorMessage = "Lỗi server";
                        }
                    }

                    Toasty.warning(requireContext(), errorMessage, Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<String>> call, Throwable t) {
                Toasty.error(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void showBookNotOwnedDialog() {
        // Build message and confirm
        String message = "Bạn đang chưa sở hữu hoặc chưa mượn sách. Thêm sách vào giỏ để mượn hoặc mua sách";
        String maSachOnline = tvIsbn.getText().toString().replace("ISBN ", "").trim() + "-ON";
        Log.e("AAA", maSachOnline);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Chưa sở hữu sách")
                .setMessage(message)
                .setPositiveButton("Thêm vào giỏ", (dialog, which) -> SubBookAdapter.AddBookToCartService(requireContext(), maSachOnline))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void openEbookReader(String bookUrl)
    {
        String pdf = "https://drive.google.com/file/d/1Tj-Jo1OEdWGp9f4GBKiZtUWRc4T80RGo/view";
        String bookTitle = "Charlotte & Willbur";
        ViewBookFragment fragment = ViewBookFragment.newInstance(bookUrl, bookTitle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentClientDashboard, fragment)
                .addToBackStack(null)
                .commit();
    }
}