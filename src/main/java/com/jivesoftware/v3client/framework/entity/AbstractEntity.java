package com.jivesoftware.v3client.framework.entity;

import com.jivesoftware.v3client.framework.AbstractJiveClient;
import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.http.EndpointDef;
import com.jivesoftware.v3client.framework.http.HttpTransport;

import java.util.Map;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public abstract class AbstractEntity {

    protected final AbstractJiveClient jiveClient;
    protected Map<String,Resource<?>> resources;

    protected AbstractEntity(AbstractJiveClient jiveClient, String type) {
        if (jiveClient == null) {
            throw new NullPointerException("jiveClient");
        }
        this.jiveClient = jiveClient;
        this.type = type;
    }

/* TODO - add copy constructor later if needed, will need to be overridden in generated classes
    protected AbstractEntity(AbstractEntity copy) {
        this.jiveClient = copy.jiveClient;
        this.id = copy.id;
        this.resourceEntities = copy.resourceEntities;
        this.resources = copy.resources;
        this.type = copy.type;
        // todo
    }
*/

    protected <T> T property(String name, Class<T> type) {
        return null; // todo
    }

    // All Core API entities have the following fields available

    protected String id;
    protected Map<String, ResourceEntity> resourceEntities;
    protected String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, ResourceEntity> getResources() {
        return this.resourceEntities;
    }

    public void setResources(Map<String, ResourceEntity> resources) {
        this.resourceEntities = resources;
        // todo, also convert to Map<String,Resource<?>>
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected HttpTransport.Request buildRequest(EndpointDef endpointDef,
                                                 Iterable<NameValuePair> parameters,
                                                 Object entity) {
        return jiveClient.buildRequest(endpointDef, parameters, entity);
    }

    protected HttpTransport.Response executeImpl(HttpTransport.Request request) {
        return jiveClient.executeImpl(request);
    }

    protected void optionalParam(NameValuePair.Builder builder, String name, Object value) {
        if (value == null || "".equals(value)) return;
        if (value instanceof AbstractEntity) value = ((AbstractEntity)value).getId();
        if (value != null) {
            builder.add(name, String.valueOf(value));
        }
    }

    protected void optionalParam(NameValuePair.Builder builder, Iterable<NameValuePair> merge) {
        if (merge == null) return;
        builder.add(merge);
    }
}
