package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.ErrorResponse;
import com.jivesoftware.v3client.framework.entity.Entities;
import com.jivesoftware.v3client.framework.type.EntityTypeLibrary;
import com.jivesoftware.v3client.framework.NameValuePair;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public interface HttpTransport {

    enum Method {

        GET("GET"),
        PUT("PUT"),
        POST("POST"),
        DELETE("DELETE");

        Method(String verb) {
            this.verb = verb;
        }

        private String verb;

        public String verb() {
            return this.verb;
        }

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

        // For debugging
        String getBodyAsString();
    }

    Response execute(Request request) throws ErrorResponse;

}
