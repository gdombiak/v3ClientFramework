package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.AbstractJiveClient;
import com.jivesoftware.v3client.framework.ErrorResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * <p>Implementation of {@link HttpTransport} based on Apache HttpClient.</p>
 */
public class HttpTransportImpl implements HttpTransport {

    public HttpTransportImpl() {
        this.httpClient = HttpClientBuilder.create()
            .build();
    }

    private final CloseableHttpClient httpClient;

    @Override
    public Response execute(Request request) throws ErrorResponse {
        HttpUriRequest httpRequest = ((HttpRequestImpl) request).asHttpUriRequest();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (IOException e) {
            throw new ErrorResponse(e.getMessage(), e, httpResponse.getStatusLine().getStatusCode());
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    // Oh well, we tried
                }
            }
        }
        try {
            return new HttpResponseImpl(httpResponse);
        } catch (IOException e) {
            throw new ErrorResponse(e.getMessage(), e, httpResponse.getStatusLine().getStatusCode());
        }
    }

}
