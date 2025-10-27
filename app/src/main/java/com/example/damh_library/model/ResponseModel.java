package com.example.damh_library.model;

import java.util.List;

public class ResponseModel<T> {
    private String message;
    private boolean isSuccess;
    private List<T> data;

    public String getMessage() { return message; }
    public boolean isSuccess() { return isSuccess; }
    public List<T> getData() { return data; }
}

