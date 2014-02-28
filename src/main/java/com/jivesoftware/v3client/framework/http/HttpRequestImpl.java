package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.NameValuePair;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

public class HttpRequestImpl implements HttpTransport.Request {

    private String body;
    private NameValuePair.Builder headers = NameValuePair.many();
    private HttpTransport.Method method;
    private NameValuePair.Builder queryParams = NameValuePair.many();
    private String uri;

    @Override
    public HttpRequestImpl addBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public HttpRequestImpl addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public HttpRequestImpl addMethod(HttpTransport.Method method) {
        this.method = method;
        return this;
    }

    @Override
    public HttpRequestImpl addQueryParam(String name, String value) {
        queryParams.add(name, value);
        return this;
    }

    // query parameters will be appended automatically
    @Override
    public HttpRequestImpl addUri(String uri) {
        this.uri = uri;
        return this;
    }

    public HttpUriRequest asHttpUriRequest() {
        String fullUri = decoratedUri();
        HttpUriRequest request = null;
        switch (method) {
            case GET:
                request = new HttpGet(fullUri);
                break;
            case PUT:
                request = new HttpPut(fullUri);
                break;
            case POST:
                request = new HttpPost(fullUri);
                break;
            case DELETE:
                request = new HttpDelete(fullUri);
                break;
        }
        for (NameValuePair header : headers) {
            request.addHeader(header.getName(), header.getValue());
        }
        if (!(request instanceof HttpDelete) && !request.containsHeader("Accept")) {
            request.addHeader("Accept", "application/json");
        }
        if ((request instanceof HttpEntityEnclosingRequest) && (body != null)) {
            if (!request.containsHeader("Content-Type")) {
                request.addHeader("Content-Type", "application/json;charset=UTF-8");
            }
            ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(body, Charset.forName("UTF-8")));
        }
        return request;
    }

    private String decoratedUri() {
        StringBuilder sb = new StringBuilder(uri);
        boolean first = true;
        for (NameValuePair queryParam : queryParams) {
            if (first) {
                sb.append("?");
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(queryParam.getName());
            sb.append("=");
            sb.append(queryParam.getValue());
        }
        return sb.toString();
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public Iterable<NameValuePair> getHeaders() {
        return headers;
    }

    @Override
    public HttpTransport.Method getMethod() {
        return method;
    }

    @Override
    public Iterable<NameValuePair> getQueryParams() {
        return queryParams;
    }

    @Override
    public String getUri() {
        return uri;
    }

}
