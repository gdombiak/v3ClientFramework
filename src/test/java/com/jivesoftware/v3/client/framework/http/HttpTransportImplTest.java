package com.jivesoftware.v3.client.framework.http;

import com.jivesoftware.v3client.framework.AbstractJiveClient;
import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.credentials.Credentials;
import com.jivesoftware.v3client.framework.http.EndpointDef;
import com.jivesoftware.v3client.framework.http.HttpTransport;
import com.jivesoftware.v3client.framework.http.HttpTransportImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class HttpTransportImplTest {

    private static final String TEST_JIVE_URL =
            System.getProperty("test.jive.url", "http://craig-z800.jiveland.com:8080");
    private static final String TEST_JIVE_USERNAME =
            System.getProperty("test.jive.username", "admin");
    private static final String TEST_JIVE_PASSWORD =
            System.getProperty("test.jive.password", "admin");
    private static final Boolean TEST_JIVE_ENABLED =
            Boolean.parseBoolean(System.getProperty("test.jive.enabled", "false"));

    private HttpTransportImpl httpTransport;
    private AbstractJiveClient jiveClient;

    @Before
    public void setupJiveClient() {
        httpTransport = new HttpTransportImpl();
        Credentials credentials = new TestCredentials();
        jiveClient = new TestJiveClient(TEST_JIVE_URL, credentials);
    }

    @After
    public void tearDownJiveClient() {
        this.httpTransport = null;
        this.jiveClient = null;
    }

    @Test
    public void testGetMe() throws Exception {
        if (!TEST_JIVE_ENABLED) {
            return;
        }
        EndpointDef endpointDef = getMeDef();
        NameValuePair.Builder parameters = NameValuePair.many()
                .add("personID", "@me");
        HttpTransport.Request request = jiveClient.buildRequest(endpointDef, parameters, null);
        HttpTransport.Response response = httpTransport.execute(request);
        System.out.println("OUTPUT=" + response.getBodyAsString());
    }


    private EndpointDef getMeDef() {
        EndpointDef endpointDef = new EndpointDef(HttpTransport.Method.GET,
                "/api/core/v3/people/{personID}",
                "fields",
                null,
                null);
        return endpointDef;
    }

    class TestCredentials implements Credentials {

        public TestCredentials() {
            headers.add(new NameValuePair("username", TEST_JIVE_USERNAME));
            headers.add(new NameValuePair("password", TEST_JIVE_PASSWORD));
        }

        private Collection<NameValuePair> headers = new ArrayList<NameValuePair>();

        @Override
        public Collection<NameValuePair> getHeaders() {
            return headers;
        }
    }

    class TestJiveClient extends AbstractJiveClient {
        TestJiveClient(String jiveURL, Credentials credentials) {
            super(jiveURL, credentials);
        }
    }

}
