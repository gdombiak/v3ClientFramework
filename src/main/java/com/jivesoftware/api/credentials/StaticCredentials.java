package com.jivesoftware.api.credentials;

import com.jivesoftware.api.NameValuePair;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class StaticCredentials implements Credentials {

    private final Collection<NameValuePair> headers;

    private StaticCredentials(NameValuePair... headers) {
        this.headers = Arrays.asList(headers);
    }

    public Collection<NameValuePair> getHeaders() {
        return headers;
    }

    public static StaticCredentials basic(String username, String password) {
        return null; // todo
    }

    public static StaticCredentials authHeader(String authHeader) {
        return new StaticCredentials(new NameValuePair("Authorization", authHeader));
    }

}
