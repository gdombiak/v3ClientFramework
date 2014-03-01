package com.jivesoftware.v3client.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class NameValuePair {

    public static final Iterable<NameValuePair> EMPTY = Collections.emptyList();

    private final String name;
    private final String value;

    public NameValuePair(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public interface Builder extends Iterable<NameValuePair> {
        Builder add(String name, String value);
        Builder add(Iterable<NameValuePair> merge);
    }

    public static Builder many() {
        return new Builder() {

            private final List<NameValuePair> list = new ArrayList<NameValuePair>(4);

            @Override
            public Iterator<NameValuePair> iterator() {
                return Collections.unmodifiableList(list).iterator();
            }

            @Override
            public Builder add(String name, String value) {
                list.add(new NameValuePair(name, value));
                return this;
            }

            @Override
            public Builder add(Iterable<NameValuePair> merge) {
                for (NameValuePair pair : merge) {
                    list.add(pair);
                }
                return this;
            }
        };
    }
}
