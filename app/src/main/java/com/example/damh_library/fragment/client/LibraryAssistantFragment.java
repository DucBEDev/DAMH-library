package com.example.damh_library.fragment.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damh_library.R;
import com.example.damh_library.adapter.client.ChatMessageAdapter;
import com.example.damh_library.model.ChatMessage;
import com.example.damh_library.model.request.ChatRequest;
import com.example.damh_library.model.response.ChatResponse;
import com.example.damh_library.network.ChatApiConfig;
import com.example.damh_library.network.client.ChatApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LibraryAssistantFragment extends Fragment {

    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private FloatingActionButton fabSend;
    private LinearLayout llLoading;

    private ChatMessageAdapter adapter;
    private List<ChatMessage> messageList;
    private ChatApiService chatApiService;
    private String userId;

    public LibraryAssistantFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("key_userId", "");
        
        setupRetrofit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_assistant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        showWelcomeMessage();
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

    private void initViews(View view) {
        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        etMessage = view.findViewById(R.id.etMessage);
        fabSend = view.findViewById(R.id.fabSend);
        llLoading = view.findViewById(R.id.llLoading);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        adapter = new ChatMessageAdapter(messageList);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(adapter);
    }

    private void setupListeners() {

        fabSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void showWelcomeMessage() {
        String welcomeMsg = "Xin chào! Tôi là trợ lý AI của thư viện.\n\n" +
                "Tôi có thể giúp bạn:\n" +
                "• Tìm kiếm sách theo tên, tác giả, thể loại\n" +
                "• Kiểm tra tình trạng sách trong kho\n" +
                "• Gợi ý sách phù hợp với nhu cầu\n\n" +
                "Hãy hỏi tôi bất cứ điều gì về sách trong thư viện!";
        
        ChatMessage welcomeMessage = new ChatMessage(welcomeMsg, false);
        adapter.addMessage(welcomeMessage);
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        
        if (message.isEmpty()) {
            Toasty.warning(requireContext(), "Vui lòng nhập câu hỏi", Toasty.LENGTH_SHORT).show();
            return;
        }

        ChatMessage userMessage = new ChatMessage(message, true);
        adapter.addMessage(userMessage);
        scrollToBottom();

        etMessage.setText("");
        hideKeyboard();

        llLoading.setVisibility(View.VISIBLE);

        ChatRequest request = new ChatRequest(message, userId);
        chatApiService.sendMessage(request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                llLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();
                    
                    if (chatResponse.isSuccess()) {
                        String answer = chatResponse.getAnswer();
                        ChatMessage botMessage = new ChatMessage(answer, false);
                        adapter.addMessage(botMessage);
                        scrollToBottom();
                    } else {
                        showErrorMessage("Xin lỗi, tôi không thể trả lời câu hỏi này.");
                    }
                } else {
                    showErrorMessage("Đã xảy ra lỗi khi xử lý yêu cầu.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                llLoading.setVisibility(View.GONE);
                showErrorMessage("Không thể kết nối đến server. Vui lòng thử lại sau.");
            }
        });
    }

    private void showErrorMessage(String errorMsg) {
        ChatMessage errorMessage = new ChatMessage(errorMsg, false);
        adapter.addMessage(errorMessage);
        scrollToBottom();
        Toasty.error(requireContext(), errorMsg, Toasty.LENGTH_SHORT).show();
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            rvChatMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && etMessage != null) {
            imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
        }
    }
}