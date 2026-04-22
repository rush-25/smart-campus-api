package com.smartcampus.response;

/**
 * Standard API success response envelope.
 * Wraps all successful responses with consistent metadata.
 */
public class ApiResponse<T> {

    private boolean success;
    private String  message;
    private T       data;
    private long    timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // Getters & Setters
    public boolean isSuccess()             { return success; }
    public void setSuccess(boolean success){ this.success = success; }

    public String getMessage()             { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData()                     { return data; }
    public void setData(T data)            { this.data = data; }

    public long getTimestamp()             { return timestamp; }
    public void setTimestamp(long ts)      { this.timestamp = ts; }
}
