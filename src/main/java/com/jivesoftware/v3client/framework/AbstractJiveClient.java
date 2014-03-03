package com.jivesoftware.v3client.framework;

import com.jivesoftware.v3client.framework.credentials.Credentials;
import com.jivesoftware.v3client.framework.entity.AbstractEntity;
import com.jivesoftware.v3client.framework.http.EndpointDef;
import com.jivesoftware.v3client.framework.http.HttpRequestImpl;
import com.jivesoftware.v3client.framework.http.HttpTransport;
import com.jivesoftware.v3client.framework.type.DataWriter;
import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collections;

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
        if (entity != null) {
            JSONObject object = new JSONObject();
            DataWriter.INSTANCE.writeDataBean(entity, object);
            request.addBody(object.toString(4));
        }
        return request;
    }

    /**
     * @param uri An absolute service path
     * @return The request object
     */
    public HttpTransport.Request buildGetRequest(URI uri) {
        Iterable<NameValuePair> queryParams;
        if (uri.getRawQuery() == null) {
            queryParams = Collections.emptyList();
        } else {
            queryParams = NameValuePair.many();
            for (String param : uri.getRawQuery().split("&")) {
                String[] split = param.split("=", 2);
                String name = decode(split[0]);
                String value = decode(split[1]);
                ((NameValuePair.Builder)queryParams).add(name, value);
            }
        }
        HttpTransport.Request request = (new HttpRequestImpl())
                .addHeader(httpCredentials.getName(), httpCredentials.getValue())
                .addMethod(HttpTransport.Method.GET)
                .addUri(resolveUri(uri.getPath()));
        if (queryParams != null) {
            for (NameValuePair queryParam : queryParams) {
                request.addQueryParam(queryParam.getName(), queryParam.getValue());
            }
        }
        return request;
    }

    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
        return resolveUri(endpointDef.getPath(parameters));
    }

    private String resolveUri(String uri) {
        if (uri.startsWith("/")) {
            uri = jiveURL + uri;
        }
        return uri;
    }

    public HttpTransport.Response executeImpl(HttpTransport.Request request) {
        AbstractJiveClient prev = JIVE_CLIENT.get();
        try {
            JIVE_CLIENT.set(this);
            return transport.execute(request);
        } finally {
            JIVE_CLIENT.set(prev);
        }
    }

    protected static String[] queryParams(String... params) {
        return params;
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
