package com.jivesoftware.api.entity;

import com.jivesoftware.api.http.HttpTransport;

import java.net.URL;
import java.util.List;

/**
 * Created by ed.venaglia on 2/27/14.
 * @param <ENTITY> the type of the entity on which this resource operates
 */
public class Resource<ENTITY> {

    private String ref;
    private List<String> allowed;

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setAllowed(List<String> allowed) {
        this.allowed = allowed;
    }

    public String name() {
        return null; // todo
    }

    public Class<ENTITY> type() {
        return null; // todo
    }

    public boolean can(HttpTransport.Method method) {
        return false; // todo
    }

    public URL url() {
        return null; // todo
    }

    public HttpTransport.Request get() {
        return null; // todo
    }

    public HttpTransport.Request put(ENTITY entity) {
        return null; // todo
    }

    public HttpTransport.Request post(ENTITY entity) {
        return null; // todo
    }

    public HttpTransport.Request delete() {
        return null; // todo
    }
}