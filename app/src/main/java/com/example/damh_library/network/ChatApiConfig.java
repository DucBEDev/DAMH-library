package com.example.damh_library.network;

public class ChatApiConfig {
    // Base URL cho ngrok - thay đổi URL này mỗi khi ngrok restart
    public static final String CHAT_BASE_URL = "https://hot-guinea-amazingly.ngrok-free.app/";
    
    // Headers cần thiết cho ngrok
    public static final String NGROK_SKIP_BROWSER_WARNING = "ngrok-skip-browser-warning";
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_AGENT_VALUE = "Android-App";
    
    // Timeout settings (giây)
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
}
