package com.example.damh_library.fragment.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.ChatMessageAdapter;
import com.example.damh_library.model.ChatMessage;
import com.example.damh_library.model.request.PdfChatRequest;
import com.example.damh_library.model.response.ChatResponse;
import com.example.damh_library.network.ChatApiConfig;
import com.example.damh_library.network.client.ChatApiService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PdfReaderFragment extends Fragment {

    private static final String TAG = "PdfReaderFragment";
    private static final String ARG_PDF_FILENAME = "pdf_filename";

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private LinearLayout llLoading;
    private TextView tvBookName;

    private ChatMessageAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private ChatApiService chatApiService;
    private String currentPdfFilename = "";
    private String userId;

    public static PdfReaderFragment newInstance(String pdfFilename) {
        PdfReaderFragment fragment = new PdfReaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_FILENAME, pdfFilename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentPdfFilename = getArguments().getString(ARG_PDF_FILENAME, "");
            Log.d(TAG, "PDF Filename: " + currentPdfFilename);
        }

        // Get user ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("key_userId", "");

        setupRetrofit();
    }

    private void setupRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request request = chain.request().newBuilder()
                            .addHeader(ChatApiConfig.NGROK_SKIP_BROWSER_WARNING, "true")
                            .addHeader(ChatApiConfig.USER_AGENT, ChatApiConfig.USER_AGENT_VALUE)
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ChatApiConfig.CHAT_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatApiService = retrofit.create(ChatApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        if (tvBookName != null && currentPdfFilename != null && !currentPdfFilename.isEmpty()) {
            // Display filename without extension
            String displayName = currentPdfFilename.replace(".pdf", "");
            tvBookName.setText(displayName);
        }

        // Show welcome message
        addBotMessage("👋 Xin chào! Tôi là trợ lý đọc sách AI.\n\n" +
                "Tôi có thể giúp bạn:\n" +
                "• Tìm kiếm thông tin trong sách\n" +
                "• Tóm tắt nội dung\n" +
                "• Trả lời câu hỏi về sách\n\n" +
                "Hãy hỏi tôi bất cứ điều gì! 📚");
    }

    private void initViews(View view) {
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        llLoading = view.findViewById(R.id.llLoading);
//        tvBookName = view.findViewById(R.id.tvBookName);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatMessageAdapter(messages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);

        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPdfFilename == null || currentPdfFilename.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy file PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message
        addUserMessage(messageText);
        etMessage.setText("");
        showLoading(true);

        // Create request with filename for PDF chat
        PdfChatRequest request = new PdfChatRequest(currentPdfFilename, messageText, userId);

        Log.d(TAG, "Sending request - Filename: " + currentPdfFilename + ", Message: " + messageText);

        // Call API
        Call<ChatResponse> call = chatApiService.sendPdfMessage(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatResponse> call,
                                   @NonNull Response<ChatResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();

                    if (chatResponse.isSuccess()) {
                        String answer = chatResponse.getAnswer();
                        Log.d(TAG, "Response received - Answer: " + answer);
                        addBotMessage(answer);
                    } else {
                        String errorMsg = "Không thể trả lời câu hỏi này";
                        addBotMessage(errorMsg);
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    addBotMessage("Xin lỗi, đã xảy ra lỗi. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatResponse> call,
                                  @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                addBotMessage("Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.");
            }
        });
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true);
        chatAdapter.addMessage(chatMessage);
        scrollToBottom();
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false);
        chatAdapter.addMessage(chatMessage);
        scrollToBottom();
    }

    private void showLoading(boolean show) {
        if (llLoading != null) {
            llLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void scrollToBottom() {
        if (rvMessages != null && messages.size() > 0) {
            rvMessages.post(() ->
                    rvMessages.smoothScrollToPosition(messages.size() - 1)
            );
        }
    }
}