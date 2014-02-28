package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.ErrorResponse;
import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.entity.Entities;
import com.jivesoftware.v3client.framework.type.EntityTypeLibrary;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

/**
 * <p>Implementation of {@link HttpTransport} based on Apache HttpClient.</p>
 */
public class HttpTransportImpl implements HttpTransport {

    @Override
    public Response execute(Request request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    class RequestImpl implements HttpTransport.Request {

        private String body;
        private NameValuePair.Builder headers = NameValuePair.many();
        private Method method;
        private NameValuePair.Builder queryParams = NameValuePair.many();
        private String uri;

        @Override
        public RequestImpl addBody(String body) {
            this.body = body;
            return this;
        }

        @Override
        public RequestImpl addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        @Override
        public RequestImpl addMethod(Method method) {
            this.method = method;
            return this;
        }

        @Override
        public RequestImpl addQueryParam(String name, String value) {
            queryParams.add(name, value);
            return this;
        }

        // query parameters will be appended automatically
        @Override
        public RequestImpl addUri(String uri) {
            this.uri = uri;
            return this;
        }

        public HttpRequest asHttpRequest() {
            String fullUri = decoratedUri();
            HttpRequest request = null;
            switch (method) {
                case GET:
                    request = new HttpGet(uri);
                    break;
                case PUT:
                    request = new HttpPut(uri);
                    break;
                case POST:
                    request = new HttpPost(uri);
                    break;
                case DELETE:
                    request = new HttpDelete(uri);
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
        public Method getMethod() {
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

    class ResponseImpl implements HttpTransport.Response {

        @Override
        public int getStatus() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Iterable<NameValuePair> getHeaders() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <ENTITY> Entities<ENTITY> getEntities(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <ENTITY> ENTITY getBody(EntityTypeLibrary<? extends ENTITY> typeLibrary) throws ErrorResponse {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }

}
