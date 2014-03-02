package com.jivesoftware.v3client.framework.http;

import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.type.EntityType;
import com.jivesoftware.v3client.framework.type.EntityTypeLibrary;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class EndpointDef {

    private final HttpTransport.Method method;
    private final String path;
    private final Set<String> pathParams;
    private final Set<String> queryParams;
    private final String bodyTypeName;
    private EntityType<?> bodyType;
    private boolean bodyTypeResolved;
    private final Iterable<NameValuePair> overrides;
    private final boolean formatExtraParamsAsFilters;

    /**
     * @param method The HTTP method
     * @param path An absolute service path with optional tokens like "/api/core/v3/contents/{id}"
     * @param queryParams String with known query param names in a comma separated list
     * @param overrides Parameter overrides in effect for this endpoint
     * @param bodyTypeName Type name of entity passed in body, or null if none
     */
    public EndpointDef(HttpTransport.Method method,
                       String path,
                       String[] queryParams,
                       String bodyTypeName,
                       Iterable<NameValuePair> overrides) {
        this.method = method;
        this.path = path;
        this.bodyTypeName = bodyTypeName;
        this.bodyTypeResolved = bodyTypeName != null && !bodyTypeName.equals("void");
        this.overrides = overrides;
        this.pathParams = extractPathParams(path);
        this.queryParams = (queryParams != null) ? new LinkedHashSet<>(Arrays.asList(queryParams)) : new LinkedHashSet<String>();
        this.formatExtraParamsAsFilters = this.queryParams.contains("filter");
        if (this.formatExtraParamsAsFilters) {
            this.queryParams.remove("filter");
        }
    }

    /**
     * Builds an EndpointDef for performing simple gets, where query params are implicitly overridden from a URI
     * @param uri The URL to invoke
     */
    public EndpointDef(URI uri) {
        this.method = HttpTransport.Method.GET;
        this.path = uri.getPath();
        this.bodyTypeName = null;
        this.bodyTypeResolved = true;
        this.pathParams = Collections.emptySet();
        if (uri.getRawQuery() == null) {
            this.overrides = Collections.emptySet();
            this.queryParams = Collections.emptySet();
        } else {
            NameValuePair.Builder overrides = NameValuePair.many();
            this.overrides = overrides;
            this.queryParams = new LinkedHashSet<>();
            for (String param : uri.getRawQuery().split("&")) {
                String[] split = param.split("=", 2);
                String name = decode(split[0]);
                String value = decode(split[1]);
                overrides.add(name, value);
                queryParams.add(name);
            }
        }
        this.formatExtraParamsAsFilters = this.queryParams.contains("filter");
        if (this.formatExtraParamsAsFilters) {
            this.queryParams.remove("filter");
        }
    }

    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> extractPathParams(String path) {
        Set<String> pathParams = new LinkedHashSet<>();
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
        if (!bodyTypeResolved) {
            bodyType = EntityTypeLibrary.ROOT.lookupByType(bodyTypeName);
            bodyTypeResolved = true;
        }
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
