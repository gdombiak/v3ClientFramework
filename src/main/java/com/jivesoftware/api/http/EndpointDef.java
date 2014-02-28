package com.jivesoftware.api.http;

import com.jivesoftware.api.NameValuePair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class EndpointDef {

    private final HttpTransport.Method method;
    private final String path;
    private final Set<String> queryParams;
    private final Iterable<NameValuePair> overrides;
    private final boolean formatExtraParamsAsFilters;

    /**
     * @param method The HTTP method
     * @param path An absolute service path with optional tokens like "/api/core/v3/contents/{id}"
     * @param queryParams String with known query param names in a comma separated list
     * @param overrides Parameter overrides in effect for this endpoint
     */
    public EndpointDef(HttpTransport.Method method,
                       String path,
                       String queryParams,
                       Iterable<NameValuePair> overrides) {
        this.method = method;
        this.path = path;
        this.overrides = overrides;
        this.formatExtraParamsAsFilters = queryParams.contains("filter");
        this.queryParams = new HashSet<String>(Arrays.asList(queryParams.split(",")));
        if (this.formatExtraParamsAsFilters) {
            this.queryParams.remove("filter");
        }
        // todo parse path for tokens, save path params
    }

    public Iterable<NameValuePair> resolveOverrides(Object thisEntity, Object bodyEntity) {
        return overrides; // todo some overrides have values that need special interpretation, see @ParamOverride
    }

    public HttpTransport.Method getMethod() {
        return method;
    }

    public Iterable<NameValuePair> getQueryParams(Iterable<NameValuePair> allParameters) {
        // exclude path params, convert filter params
        return null; // todo
    }

    public String getPath(Iterable<NameValuePair> allParameters) {
        // replace tokens in path
        return path; // todo
    }
}
