package com.smartcampus.response;

/**
 * Standardised error response body.
 * All exception mappers use this to return consistent, safe JSON error messages
 * without leaking internal Java stack traces.
 */
public class ErrorResponse {

    private int    status;
    private String error;
    private String message;
    private String path;
    private long   timestamp;

    public ErrorResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(int status, String error, String message) {
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message);
        this.path = path;
    }

    // Getters & Setters
    public int    getStatus()              { return status; }
    public void   setStatus(int status)   { this.status = status; }

    public String getError()               { return error; }
    public void   setError(String error)  { this.error = error; }

    public String getMessage()             { return message; }
    public void   setMessage(String msg)  { this.message = msg; }

    public String getPath()               { return path; }
    public void   setPath(String path)    { this.path = path; }

    public long   getTimestamp()          { return timestamp; }
    public void   setTimestamp(long ts)   { this.timestamp = ts; }
}
