package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.AbstractJiveClient;
import com.jivesoftware.v3client.framework.ErrorResponse;
import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.entity.Entities;
import com.jivesoftware.v3client.framework.type.DataReader;
import com.jivesoftware.v3client.framework.type.DuckType;
import com.jivesoftware.v3client.framework.type.EntityType;
import com.jivesoftware.v3client.framework.type.EntityTypeLibrary;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpResponseImpl implements HttpTransport.Response {

    public HttpResponseImpl(HttpResponse response) throws IOException {
        this.response = response;
        HttpEntity entity = this.response.getEntity();
        if (entity != null) {
            this.body = EntityUtils.toString(entity, Charset.forName("UTF-8"));
        } else {
            this.body = null;
        }
    }

    private final String body;
    private final HttpResponse response;
    private DuckType duckType;

    @Override
    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public Iterable<NameValuePair> getHeaders() {
        NameValuePair.Builder headers = NameValuePair.many();
        for (Header header : response.getAllHeaders()) {
            headers.add(header.getName(), header.getValue());
        }
        return headers;
    }

    @Override
    public <ENTITY> Iterable<ENTITY> getEntities(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse {
        DuckType duckType = getDuckType();
        switch (duckType.getQuacksLike()) {
            case ERROR:
                throw buildError(duckType.getObject());
            case ENTITY:
                return Collections.singleton(getBody(typeLibrary));
            case ENTITIES:
                Entities<ENTITY> entities = parseEntities(duckType.getObject(), typeLibrary);
                entities.load(parseEntities(duckType.getArray(), typeLibrary));
                return entities;
            case OBJECT:
                break;
            case ARRAY:
                break;
            case UNKNOWN:
                throw new ErrorResponse("Unable to parse response", response.getStatusLine().getStatusCode(), getHeaders());
        }
        return null;
    }

    private <ENTITY> Entities<ENTITY> parseEntities(JSONObject object, EntityTypeLibrary<? extends ENTITY> typeLibrary) {
        Entities<ENTITY> entities = new Entities<>(AbstractJiveClient.JIVE_CLIENT.get(), typeLibrary);
        DataReader.INSTANCE.get().readDataBean(object, entities);
        return entities;
    }

    private <ENTITY> List<ENTITY> parseEntities(JSONArray array, EntityTypeLibrary<? extends ENTITY> typeLibrary) {
        List<ENTITY> entities = new ArrayList<>(array.length());
        for (int i = 0, l = array.length(); i < l; i++) {
            JSONObject json = array.optJSONObject(i);
            ENTITY object = null;
            if (json != null) {
                DuckType duckType = new DuckType(json);
                if (duckType.getQuacksLike() == DuckType.QuacksLike.ENTITY) {
                    EntityType<? extends ENTITY> type = typeLibrary.lookupByType(duckType.getEntityType());
                    if (type != null) {
                        object = parseEntity(json, type);
                    }
                }
            }
            entities.add(object);
        }
        return entities;
    }

    private <ENTITY> ENTITY parseEntity(JSONObject object, EntityType<? extends ENTITY> entityType) {
        return DataReader.INSTANCE.get().readDataBean(object, entityType.getType());
    }

    @Override
    public <ENTITY> ENTITY getBody(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse {
        DuckType duckType = getDuckType();
        switch (duckType.getQuacksLike()) {
            case ERROR:
                throw buildError(duckType.getObject());
            case ENTITY:
                EntityType<? extends ENTITY> entityType = typeLibrary.lookupByType(duckType.getEntityType());
                if (entityType != null) {
                    return DataReader.INSTANCE.get().readDataBean(duckType.getObject(), entityType.getType());
                }
                break;
            case ENTITIES:
            case ARRAY:
                throw new ClassCastException(String.format("Cannot cast Iterable<%1$s> to %1$s; call getEntities() instead", typeLibrary.getSuperType()));
            case OBJECT:
                if (typeLibrary.isPossible(JSONObject.class)) {
                    typeLibrary.cast(duckType.getObject());
                }
                break;
            case UNKNOWN:
                throw new ErrorResponse("Unable to parse response", response.getStatusLine().getStatusCode(), getHeaders());
        }
        return null;
    }

    private ErrorResponse buildError(JSONObject object) {
        String message = object.optString("message");
        int status = object.optInt("status");
        String code = object.optString("code", null);
        return new ErrorResponse(message, status, getHeaders()).setCode(code);
    }

    private DuckType getDuckType() {
        if (duckType == null) {
            duckType = new DuckType(body);
            if (duckType.getQuacksLike() == DuckType.QuacksLike.DATA_WRAPPER) {
                duckType = duckType.getWrapped();
            }
        }
        return duckType;
    }

    // For debugging
    public String getBodyAsString() {
        return this.body;
    }

}
