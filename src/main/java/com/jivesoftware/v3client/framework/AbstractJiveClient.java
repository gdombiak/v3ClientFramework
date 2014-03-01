package com.jivesoftware.v3client.framework;

import com.jivesoftware.v3client.framework.credentials.Credentials;
import com.jivesoftware.v3client.framework.entity.AbstractEntity;
import com.jivesoftware.v3client.framework.http.EndpointDef;
import com.jivesoftware.v3client.framework.http.HttpRequestImpl;
import com.jivesoftware.v3client.framework.http.HttpTransport;
import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

/**
 * <p>The real JiveClient object extends this class (to implement static methods), and is generated from
 * the ObjectMetadata of the Core API.</p>
 */
public abstract class AbstractJiveClient {

    public static final ThreadLocal<AbstractJiveClient> JIVE_CLIENT = new ThreadLocal<>();

    protected AbstractJiveClient(String jiveURL, Credentials credentials, HttpTransport transport) {
        this.jiveURL = jiveURL;
        this.credentials = credentials;
        this.transport = transport;
        this.httpCredentials = extractCredentials();
    }

    private final Credentials credentials;
    private final Header httpCredentials;
    protected final HttpTransport transport;
    protected final String jiveURL;

    /**
     * @param endpointDef An absolute service path with optional tokens like "/api/core/v3/contents/{id}"
     * @param parameters A number of name value pairs that comprise dynamic elements of this request
     * @param entity The entity to send in the body of the request, or null if there is no body
     * @return The request object
     */
    public HttpTransport.Request buildRequest(EndpointDef endpointDef,
                                                 Iterable<NameValuePair> parameters,
                                                 Object entity) {
        HttpTransport.Request request = (new HttpRequestImpl())
                .addHeader(httpCredentials.getName(), httpCredentials.getValue())
                .addMethod(endpointDef.getMethod())
                .addUri(resolveUri(endpointDef, parameters));
        Iterable<NameValuePair> queryParams = endpointDef.getQueryParams(parameters);
        if (queryParams != null) {
            for (NameValuePair queryParam : queryParams) {
                request.addQueryParam(queryParam.getName(), queryParam.getValue());
            }
        }
        // TODO - deal with body conversion from entities that are not strings
        if (entity != null) {
            request.addBody(entity.toString());
        }
        return request;
    }

    // TODO - for now, only basic auth is supported
    private Header extractCredentials() {
        String username = null;
        String password = null;
        for (NameValuePair pair : credentials.getHeaders()) {
            if (pair.getName().equals("username")) {
                username = pair.getValue();
            } else if (pair.getName().equals("password")) {
                password = pair.getValue();
            }
        }
        if (username == null) {
            if (password == null) {
                throw new IllegalArgumentException("Missing 'username' and 'password' credentials");
            } else {
                throw new IllegalArgumentException("Missing 'username' credential");
            }
        } else if (password == null) {
            throw new IllegalArgumentException("Missing 'password' credential");
        }
        return BasicScheme.authenticate(new UsernamePasswordCredentials(username, password), "UTF-8", false);
    }

    private String resolveUri(EndpointDef endpointDef, Iterable<NameValuePair> parameters) {
        String uri = endpointDef.getPath(parameters);
        if (uri.startsWith("/")) {
            uri = jiveURL + uri;
        }
        return uri;
    }

    public HttpTransport.Response executeImpl(HttpTransport.Request request) {
        return transport.execute(request);
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
