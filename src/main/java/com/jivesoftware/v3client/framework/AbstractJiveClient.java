package com.jivesoftware.v3client.framework;

import com.jivesoftware.v3client.framework.credentials.Credentials;
import com.jivesoftware.v3client.framework.http.EndpointDef;
import com.jivesoftware.v3client.framework.http.HttpTransport;

import java.net.URL;

/**
 * The real JiveClient object extends this class, and is generated from the ObjectMetadata of the core api
 */
public abstract class AbstractJiveClient {

    protected AbstractJiveClient() {
    }

    protected void init(URL jiveURL, Credentials credentials) {
        // todo
    }

    /**
     * @param endpointDef An absolute service path with optional tokens like "/api/core/v3/contents/{id}"
     * @param parameters A number of name value pairs that comprise dynamic elements of this request
     * @param entity The entity to send in the body of the request, or null if there is no body
     * @return The request object
     */
    protected HttpTransport.Request buildRequest(EndpointDef endpointDef,
                                                 Iterable<NameValuePair> parameters,
                                                 Object entity) {
        return null; // todo
    }
}
