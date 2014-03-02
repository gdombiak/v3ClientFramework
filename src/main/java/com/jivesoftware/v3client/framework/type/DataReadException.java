package com.jivesoftware.v3client.framework.type;

/**
 * Created by ed.venaglia on 3/1/14.
 */
public class DataReadException extends RuntimeException {

    public DataReadException() {
    }

    public DataReadException(String message) {
        super(message);
    }

    public DataReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataReadException(Throwable cause) {
        super(cause);
    }

    public DataReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
