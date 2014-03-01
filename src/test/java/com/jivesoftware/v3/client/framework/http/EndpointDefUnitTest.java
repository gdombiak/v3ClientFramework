package com.jivesoftware.v3.client.framework.http;

import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.http.EndpointDef;
import com.jivesoftware.v3client.framework.http.HttpTransport;
import org.junit.Test;

import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EndpointDefUnitTest {

    @Test
    public void testDeleteFollowing() throws Exception {
        EndpointDef endpointDef = deleteFollowingDef();
        assertEquals(null, endpointDef.getBodyType());
        assertEquals(HttpTransport.Method.DELETE, endpointDef.getMethod());
        NameValuePair.Builder pathParameters = NameValuePair.many()
                .add("personID", "1234")
                .add("followedPersonID", "5678");
        assertEquals("/api/core/v3/people/1234/@following/5678", endpointDef.getPath(pathParameters));
    }

    @Test
    public void testGetPerson() throws Exception {
        EndpointDef endpointDef = getPersonDef();
        assertEquals(null, endpointDef.getBodyType());
        assertEquals(HttpTransport.Method.GET, endpointDef.getMethod());
        NameValuePair.Builder pathParameters = NameValuePair.many()
                .add("personID", "1234");
        String result = endpointDef.getPath(pathParameters);
        assertEquals("/api/core/v3/people/1234", result);
        NameValuePair.Builder queryParameters = NameValuePair.many()
                .add("fields", "id,displayName,-resources,type");
        Iterable<NameValuePair> results = endpointDef.getQueryParams(queryParameters);
        assertNameValuePairs(encodeValues(queryParameters), results);
    }

    private void assertNameValuePairs(Iterable<NameValuePair> expected, Iterable<NameValuePair> actual) {
        for (NameValuePair expect : expected) {
            boolean found = false;
            for (NameValuePair act : actual) {
                if (expect.getName().equals(act.getName()) && expect.getValue().equals(act.getValue())) {
                    found = true;
                    break;
                }
            }
            assertTrue("Found name '" + expect.getName() + "' and value '" + expect.getValue() + "'", found);
        }
    }

    private Iterable<NameValuePair> encodeValues(Iterable<NameValuePair> inputs) throws Exception {
        NameValuePair.Builder outputs = NameValuePair.many();
        for (NameValuePair input : inputs) {
            outputs.add(input.getName(), URLEncoder.encode(input.getValue(), "UTF-8"));
        }
        return outputs;
    }

    private EndpointDef deleteFollowingDef() {
        EndpointDef endpointDef = new EndpointDef(HttpTransport.Method.DELETE,
                "/api/core/v3/people/{personID}/@following/{followedPersonID}",
                null,
                null,
                null);
        return endpointDef;
    }

    private EndpointDef getPersonDef() {
        EndpointDef endpointDef = new EndpointDef(HttpTransport.Method.GET,
                "/api/core/v3/people/{personID}",
                "fields",
                null, // TODO - should be PersonEntityType
                null);
        return endpointDef;
    }

}
