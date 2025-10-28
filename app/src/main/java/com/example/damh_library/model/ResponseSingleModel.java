package com.example.damh_library.model;

public class ResponseSingleModel<T> {
    private String message;
    private boolean isSuccess;
    private T data;

    public String getMessage() { return message; }
    public boolean isSuccess() { return isSuccess; }
    public T getData() { return data; }
}

