package com.jivesoftware.api.http;

import com.jivesoftware.api.ErrorResponse;
import com.jivesoftware.api.entity.Entities;
import com.jivesoftware.api.type.EntityTypeLibrary;
import com.jivesoftware.api.NameValuePair;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public interface HttpTransport {

    enum Method {
        GET, PUT, POST, DELETE
    }

    interface Request {

        Method getMethod();

        Iterable<NameValuePair> getHeaders();

        Iterable<NameValuePair> getQueryParams();

        String getBody();
    }

    interface Response {

        int getStatus();

        Iterable<NameValuePair> getHeaders();

        <ENTITY> Entities<ENTITY> getEntities(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse;

        <ENTITY> ENTITY getBody(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse;
    }

    Response execute(Request request);
}
