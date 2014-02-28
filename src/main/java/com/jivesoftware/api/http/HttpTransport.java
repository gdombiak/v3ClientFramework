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

        Request addBody(String body);

        Request addHeader(String name, String value);

        Request addMethod(Method method);

        Request addQueryParam(String name, String value);

        Request addUri(String uri);

        String getBody();

        Iterable<NameValuePair> getHeaders();

        Method getMethod();

        Iterable<NameValuePair> getQueryParams();

        String getUri();

    }

    interface Response {

        int getStatus();

        Iterable<NameValuePair> getHeaders();

        <ENTITY> Entities<ENTITY> getEntities(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse;

        <ENTITY> ENTITY getBody(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse;
    }

    Response execute(Request request);
}
