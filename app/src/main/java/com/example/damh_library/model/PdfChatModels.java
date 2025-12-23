package com.example.damh_library.model;

import java.util.List;

public class PdfChatModels {
    
    public static class PdfChatRequest {
        private String filename;
        private String message;
        private String user_id;

        public PdfChatRequest(String filename, String message, String user_id) {
            this.filename = filename;
            this.message = message;
            this.user_id = user_id;
        }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUser_id() { return user_id; }
        public void setUser_id(String user_id) { this.user_id = user_id; }
    }

    public static class PdfChatResponse {
        private String answer;
        private List<Citation> citations;
        private String status;

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public List<Citation> getCitations() { return citations; }
        public void setCitations(List<Citation> citations) { this.citations = citations; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class Citation {
        private String ref_id;
        private int page;
        private String bboxes;
        private String text_preview;

        public String getRef_id() { return ref_id; }
        public void setRef_id(String ref_id) { this.ref_id = ref_id; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public String getBboxes() { return bboxes; }
        public void setBboxes(String bboxes) { this.bboxes = bboxes; }
        public String getText_preview() { return text_preview; }
        public void setText_preview(String text_preview) { this.text_preview = text_preview; }
    }

    public static class ChatMessage {
        private String message;
        private boolean isUser;
        private long timestamp;
        private List<Citation> citations;

        public ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
            this.timestamp = System.currentTimeMillis();
            this.citations = null;
        }

        public ChatMessage(String message, boolean isUser, List<Citation> citations) {
            this.message = message;
            this.isUser = isUser;
            this.timestamp = System.currentTimeMillis();
            this.citations = citations;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public boolean isUser() { return isUser; }
        public void setUser(boolean user) { isUser = user; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public List<Citation> getCitations() { return citations; }
        public void setCitations(List<Citation> citations) { this.citations = citations; }
    }
}
