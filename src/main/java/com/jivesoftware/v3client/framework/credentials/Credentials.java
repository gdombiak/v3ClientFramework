package com.jivesoftware.v3client.framework.credentials;

import com.jivesoftware.v3client.framework.NameValuePair;

import java.util.Collection;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public interface Credentials {
    Collection<NameValuePair> getHeaders();
}
