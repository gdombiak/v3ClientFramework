package com.jivesoftware.v3client.framework.type;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class EntityType<T> {

    private final Class<T> type;
    private final String name;
    private final String plural;

    public EntityType(Class<T> type, String name, String plural) {
        this.type = type;
        this.name = name;
        this.plural = plural;
    }

    public Class<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPlural() {
        return plural;
    }
}
