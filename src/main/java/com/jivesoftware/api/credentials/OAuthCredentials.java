package com.jivesoftware.api.credentials;

import com.jivesoftware.api.NameValuePair;
import com.jivesoftware.api.http.HttpTransport;

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
