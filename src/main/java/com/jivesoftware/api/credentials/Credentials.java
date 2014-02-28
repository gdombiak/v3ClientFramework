package com.jivesoftware.api.credentials;

import com.jivesoftware.api.NameValuePair;

import java.util.Collection;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public interface Credentials {
    Collection<NameValuePair> getHeaders();
}
