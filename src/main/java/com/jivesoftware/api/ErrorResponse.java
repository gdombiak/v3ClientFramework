package com.jivesoftware.api;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class ErrorResponse extends RuntimeException {

    private int status;
    private Iterable<NameValuePair> headers;

    public ErrorResponse(String message, int status) {
        super(message);
        this.status = status;
        this.headers = null;
    }

    public ErrorResponse(String message, int status, Iterable<NameValuePair> headers) {
        super(message);
        this.status = status;
        this.headers = headers;
    }

    public ErrorResponse(String message, Throwable cause, int status, Iterable<NameValuePair> headers) {
        super(message, cause);
        this.status = status;
        this.headers = headers;
    }

    public ErrorResponse(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
        this.headers = null;
    }

    public int getStatus() {
        return status;
    }

    public Iterable<NameValuePair> getHeaders() {
        return headers;
    }
}
