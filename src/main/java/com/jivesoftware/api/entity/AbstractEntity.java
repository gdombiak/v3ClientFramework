package com.jivesoftware.api.entity;

import com.jivesoftware.api.AbstractJiveClient;
import com.jivesoftware.api.type.EntityType;

import java.util.Map;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public abstract class AbstractEntity {

    protected final AbstractJiveClient jiveClient;
    protected Map<String,Resource<?>> resources;

    protected AbstractEntity(AbstractJiveClient jiveClient) {
        this.jiveClient = jiveClient;
    }

    protected AbstractEntity(AbstractEntity copy) {
        this.jiveClient = copy.jiveClient;
        // todo
    }

    protected <T> T property(String name, Class<T> type) {
        return null; // todo
    }

    protected abstract EntityType<?> lookupResourceType(String resourceName);

    public void setResources(Map<String,Object> resources) {
        // todo, convert to Map<String,Resource<?>>
    }
}
