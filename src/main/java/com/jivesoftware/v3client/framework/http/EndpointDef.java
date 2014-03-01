package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.type.EntityType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class EndpointDef {

    private final HttpTransport.Method method;
    private final String path;
    private final Set<String> pathParams;
    private final Set<String> queryParams;
    private final EntityType<?> bodyType;
    private final Iterable<NameValuePair> overrides;
    private final boolean formatExtraParamsAsFilters;

    /**
     * @param method The HTTP method
     * @param path An absolute service path with optional tokens like "/api/core/v3/contents/{id}"
     * @param queryParams String with known query param names in a comma separated list
     * @param overrides Parameter overrides in effect for this endpoint
     * @param bodyType Type of entity passed in body, or null if none
     */
    public EndpointDef(HttpTransport.Method method,
                       String path,
                       String queryParams,
                       EntityType<?> bodyType,
                       Iterable<NameValuePair> overrides) {
        this.method = method;
        this.path = path;
        this.bodyType = bodyType;
        this.overrides = overrides;
        this.formatExtraParamsAsFilters = (queryParams != null) ? queryParams.contains("filter") : false;
        this.pathParams = extractPathParams(path);
        this.queryParams = (queryParams != null) ? new HashSet<String>(Arrays.asList(queryParams.split(","))) : new HashSet<String>();
        if (this.formatExtraParamsAsFilters) {
            this.queryParams.remove("filter");
        }
    }

    private Set<String> extractPathParams(String path) {
        Set<String> pathParams = new HashSet<>();
        String[] segments = path.split("/");
        for (String segment : segments) {
            if (segment.startsWith("{") && segment.endsWith("}")) {
                pathParams.add(segment.substring(1, segment.length() - 1));
            }
        }
        return pathParams;
    }

    public Iterable<NameValuePair> resolveOverrides(Object thisEntity, Object bodyEntity) {
        return overrides; // todo some overrides have values that need special interpretation, see @ParamOverride
    }

    public HttpTransport.Method getMethod() {
        return method;
    }

    public Iterable<NameValuePair> getQueryParams(Iterable<NameValuePair> allParameters) {
        NameValuePair.Builder queryParams = NameValuePair.many();
        for (NameValuePair pair : allParameters) {
            if (!this.queryParams.contains(pair.getName())) {
                continue;
            }
            try {
                queryParams.add(pair.getName(), URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // Can not happen
            }
        }
        // TODO - convert filter params ???
        return queryParams;
    }

    public EntityType<?> getBodyType() {
        return bodyType;
    }

    public String getPath(Iterable<NameValuePair> allParameters) {
        String path = this.path;
        for (NameValuePair pair : allParameters) {
            if (!this.pathParams.contains(pair.getName())) {
                continue;
            }
            String key = '{' + pair.getName() + '}';
            try {
                path = path.replace(key, URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // Can not happen
            }
        }
        return path;
    }
}
