package com.example.damh_library.fragment.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.damh_library.R;
import com.example.damh_library.adapter.client.SubBookAdapter;
import com.example.damh_library.model.ResponseSingleModel;
import com.example.damh_library.model.request.BookCartRequest;
import com.example.damh_library.model.response.BookDetailResponse;
import com.example.damh_library.network.ApiClient;
import com.example.damh_library.network.client.DauSachApiService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailFragment extends Fragment {

    // Views theo layout XML mới
    private Toolbar toolbar;
    private ImageView ivBookCover;
    private TextView tvBookTitle, tvAuthor, tvAvailability, tvISBN;
    private TextView tvType, tvLanguage, tvPages, tvEdition, tvPublisher, tvPublishDate;
    private TextView tvDescription, tvPrice;
    private TextView tvBookCopiesCount;
    private RecyclerView rvBookCopies;
    private LinearLayout layoutEmptyBookCopies;
    private com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton fabBorrowBook;

    private DauSachApiService apiService;
    private String isbn;

    public BookDetailFragment() {}

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

        initViews(view);
        setupToolbar();
        fetchBookDetail();

        return view;
    }

    private void initViews(View view) {
        // Toolbar
        toolbar = view.findViewById(R.id.toolbar);
        
        // Book info
        ivBookCover = view.findViewById(R.id.ivBookCover);
        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvAvailability = view.findViewById(R.id.tvAvailability);
        tvISBN = view.findViewById(R.id.tvISBN);
        
        // Detail info grid
        tvType = view.findViewById(R.id.tvType);
        tvLanguage = view.findViewById(R.id.tvLanguage);
        tvPages = view.findViewById(R.id.tvPages);
        tvEdition = view.findViewById(R.id.tvEdition);
        tvPublisher = view.findViewById(R.id.tvPublisher);
        tvPublishDate = view.findViewById(R.id.tvPublishDate);
        
        // Description and price
        tvDescription = view.findViewById(R.id.tvDescription);
        tvPrice = view.findViewById(R.id.tvPrice);
        
        // Book copies list
        tvBookCopiesCount = view.findViewById(R.id.tvBookCopiesCount);
        rvBookCopies = view.findViewById(R.id.rvBookCopies);
        layoutEmptyBookCopies = view.findViewById(R.id.layoutEmptyBookCopies);
        
        // FAB
        fabBorrowBook = view.findViewById(R.id.fabBorrowBook);
    }

    private void setupToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }
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
                    BookDetailResponse bookDetail = response.body().getData();
                    bindBookData(bookDetail);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Không có dữ liệu";
                    Toasty.error(requireContext(), "Lỗi: " + msg, Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSingleModel<BookDetailResponse>> call, Throwable t) {
                Toasty.error(requireContext(), "Không kết nối được server!", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void bindBookData(BookDetailResponse bookDetail) {
        // Load cover image
        if (bookDetail.getImagePath() != null && !bookDetail.getImagePath().isEmpty()) {
            Glide.with(requireContext())
                    .load(bookDetail.getImagePath())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(ivBookCover);
        } else {
            ivBookCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        // Basic info
        tvBookTitle.setText(bookDetail.getTitle() != null ? bookDetail.getTitle() : "Không rõ tên");
        tvAuthor.setText(bookDetail.getAuthor() != null ? bookDetail.getAuthor() : "Không rõ tác giả");
        tvISBN.setText("ISBN: " + (bookDetail.getIsbn() != null ? bookDetail.getIsbn() : "Không rõ"));

        // Availability status
        int available = bookDetail.getAvailableQuantity();
        int total = bookDetail.getQuantity();
        tvAvailability.setText("Còn " + available + "/" + total + " cuốn");

        // Detail grid
        tvType.setText(bookDetail.getType() != null ? bookDetail.getType() : "Không rõ");
        tvLanguage.setText(bookDetail.getLanguage() != null ? bookDetail.getLanguage() : "Không rõ");
        tvPages.setText(bookDetail.getNumberOfPages() != null ? bookDetail.getNumberOfPages().toString() : "0");
        tvEdition.setText("Lần " + (bookDetail.getEdition() != null ? bookDetail.getEdition().toString() : "1"));
        tvPublisher.setText(bookDetail.getPublisher() != null ? bookDetail.getPublisher() : "Không rõ");
        
        // Format publish date
        String publishDate = formatDate(bookDetail.getPublishDate());
        tvPublishDate.setText(publishDate);

        // Description
        tvDescription.setText(bookDetail.getDescription() != null ? bookDetail.getDescription() : "Chưa có mô tả");

        // Format price
        String priceText = "Miễn phí";
        if (bookDetail.getPrice() != null && bookDetail.getPrice() > 0) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            priceText = formatter.format(bookDetail.getPrice());
        }
        tvPrice.setText(priceText);

        // Book copies list
        setupBookCopiesList(bookDetail.getBookCopies());

        // FAB click listener - chỉ chuyển qua PDF viewer
        fabBorrowBook.setOnClickListener(v -> {
            isEbookStillBorrowed();
        });

        // Set toolbar title
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        }
    }

    private void setupBookCopiesList(List<BookDetailResponse.BookCopy> bookCopies) {
        if (bookCopies != null && !bookCopies.isEmpty()) {
            tvBookCopiesCount.setText(bookCopies.size() + " cuốn");
            
            SubBookAdapter subAdapter = new SubBookAdapter(requireContext(), bookCopies);
            rvBookCopies.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvBookCopies.setAdapter(subAdapter);
            
            rvBookCopies.setVisibility(View.VISIBLE);
            layoutEmptyBookCopies.setVisibility(View.GONE);
        } else {
            tvBookCopiesCount.setText("0 cuốn");
            rvBookCopies.setVisibility(View.GONE);
            layoutEmptyBookCopies.setVisibility(View.VISIBLE);
        }
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "Không rõ";
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
            Date date = inputFormat.parse(isoDate);
            if (date != null) {
                return outputFormat.format(date);
            }

        } catch (Exception e) {
        }
        return "Không rõ";
    }

    private void isEbookStillBorrowed() {
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        long maDG = Long.parseLong(prefs.getString("key_userId", "0"));

        String maSach = tvISBN.getText().toString().replace("ISBN: ", "").trim() + "-ON";

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
                        getBookUrl(tvISBN.getText().toString().replace("ISBN: ", "").trim());
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
        String message = "Bạn đang chưa sở hữu hoặc chưa mượn sách. Thêm sách vào giỏ để mượn hoặc mua sách";
        String maSachOnline = tvISBN.getText().toString().replace("ISBN: ", "").trim() + "-ON";

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
