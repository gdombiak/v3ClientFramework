package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.ErrorResponse;
import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.entity.Entities;
import com.jivesoftware.v3client.framework.type.EntityTypeLibrary;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

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
    public <ENTITY> Entities<ENTITY> getEntities(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <ENTITY> ENTITY getBody(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // For debugging
    public String getBodyAsString() {
        return this.body;
    }

}
