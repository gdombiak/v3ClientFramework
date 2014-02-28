package com.jivesoftware.v3client.framework.entity;

import com.jivesoftware.v3client.framework.http.HttpTransport;

import java.util.Set;

public class ResourceEntity {

    private String name;
    private String ref;
    private Set<HttpTransport.Method> allowed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Set<HttpTransport.Method> getAllowed() {
        return allowed;
    }

    public void setAllowed(Set<HttpTransport.Method> allowed) {
        this.allowed = allowed;
    }

}
