package com.jivesoftware.v3client.framework.credentials;

import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.http.HttpTransport;

import java.net.URL;
import java.util.Collection;
import java.util.Observable;

/**
 * Created by ed.venaglia on 2/27/14.
 *
 * ===============================================
 * === Stretch goal, not needed for this hack! ===
 * ===============================================
 */
public class OAuthCredentials extends Observable implements Credentials {

    @Override
    public Collection<NameValuePair> getHeaders() {
        return null;
    }

    public OAuthCredentials build(URL jiveURL, HttpTransport transport, String clientID, String clientSecret, String refreshToken) {
        return null; // todo
    }

}
