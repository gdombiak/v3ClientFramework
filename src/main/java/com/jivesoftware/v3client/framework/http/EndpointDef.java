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
    private final Set<String> overriddenNames;
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
        this.overriddenNames = extactNames(overrides);
        this.pathParams = extractPathParams(path);
        this.queryParams = (queryParams != null) ? new LinkedHashSet<>(Arrays.asList(queryParams)) : new LinkedHashSet<String>();
        this.formatExtraParamsAsFilters = this.queryParams.contains("filter");
        if (this.formatExtraParamsAsFilters) {
            this.queryParams.remove("filter");
        }
    }

    private Set<String> extactNames(Iterable<NameValuePair> overrides) {
        if (overrides == null || overrides == Collections.EMPTY_LIST) {
            return Collections.emptySet();
        }
        Set<String> names = new HashSet<>();
        return names;
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

    public HttpTransport.Method getMethod() {
        return method;
    }

    public Iterable<NameValuePair> getQueryParams(Iterable<NameValuePair> allParameters) {
        NameValuePair.Builder queryParams = NameValuePair.many();
        for (NameValuePair pair : allParameters) {
            if (overriddenNames.contains(pair.getName())) {
                continue;
            }
            if (this.queryParams.contains(pair.getName())) {
                try {
                    queryParams.add(pair.getName(), URLEncoder.encode(pair.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // Can not happen
                }
            }
            else if (formatExtraParamsAsFilters) {
                try {
                    String filter = String.format("%s(%s)", pair.getName(), pair.getValue());
                    String filterEncoded = URLEncoder.encode(filter, "UTF-8");
                    queryParams.add("filter", filterEncoded);
                } catch (UnsupportedEncodingException e) {
                    // Can not happen
                }
            }
        }
        if (overrides != null) {
            for (NameValuePair pair : overrides) {
                if (this.queryParams.contains(pair.getName())) {
                    try {
                        queryParams.add(pair.getName(), URLEncoder.encode(pair.getValue(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        // Can not happen
                    }
                }
                else if (formatExtraParamsAsFilters) {
                    try {
                        String filter = String.format("%s(%s)", pair.getName(), pair.getValue());
                        String filterEncoded = URLEncoder.encode(filter, "UTF-8");
                        queryParams.add("filter", filterEncoded);
                    } catch (UnsupportedEncodingException e) {
                        // Can not happen
                    }
                }
            }
        }
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
