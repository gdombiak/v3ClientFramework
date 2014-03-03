package com.jivesoftware.v3client.framework.type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ed.venaglia on 3/1/14.
 */
public class DuckType {

    private static final Pattern MATCH_THROWS = Pattern.compile("^throw\\s.*?;");

    public enum QuacksLike {
        ERROR, // error response, call getObject() for the error object
        ENTITY, // A typed entity, call getEntityType()
        ENTITIES, // call getObject() for list metadata, call getArray() for the contents
        OBJECT, // plain JSONObject, call getObject()
        ARRAY, // plain JSONArray, call getArray()
        DATA_WRAPPER, // call getWrapped() for the wrapped entity
        VOID, // Nothing was found, either no content, and empty object, or an empty array
        UNKNOWN // don't know what you found
    }

    private final QuacksLike quacksLike;

    private String entityType;
    private DuckType wrapped;
    private JSONObject object;
    private JSONArray array;

    public DuckType (String source) {
        source = source.trim();
        Matcher matcher = MATCH_THROWS.matcher(source);
        if (matcher.find()) {
            source = source.substring(matcher.end()).trim();
        }
        quacksLike = guessType(source);
    }

    public DuckType (JSONObject source) {
        this.object = source;
        quacksLike = guessType(source);
    }

    public QuacksLike getQuacksLike() {
        return quacksLike;
    }

    public String getEntityType() {
        return entityType;
    }

    public DuckType getWrapped() {
        return wrapped;
    }

    public JSONObject getObject() {
        return object;
    }

    public JSONArray getArray() {
        return array;
    }

    private QuacksLike guessType(String source) {
        if (source.length() == 0) {
            return QuacksLike.VOID;
        }
        if (source.startsWith("[") && source.endsWith("]")) {
            try {
                array = new JSONArray(source);
                return array.length() == 0 ? QuacksLike.VOID : QuacksLike.ARRAY;
            } catch (JSONException e) {
                // malformed, return UNKNOWN
            }
        }
        if (source.startsWith("{") && source.endsWith("}")) {
            try {
                object = new JSONObject(source);
                return object.length() == 0 ? QuacksLike.VOID : guessType(object);
            } catch (JSONException e) {
                // malformed, return UNKNOWN
            }
        }
        return QuacksLike.UNKNOWN;
    }

    private QuacksLike guessType(JSONObject object) {
        if (object.opt("error") instanceof JSONObject) {
            this.object = object.optJSONObject("error");
            return QuacksLike.ERROR;
        }
        if (object.opt("content") instanceof JSONObject &&
            !object.has("resources") &&
            !object.has("list") &&
            !object.has("provider")) {
            DuckType wrapped = new DuckType(object.optJSONObject("content"));
            if (wrapped.getQuacksLike() == QuacksLike.ENTITY) {
                this.wrapped = wrapped;
                return QuacksLike.DATA_WRAPPER;
            }
        }
        if (object.opt("provider") instanceof JSONObject &&
            object.opt("verb") instanceof String) {
            entityType = "activity";
            return QuacksLike.ENTITY;
        }
        if (object.opt("resources") instanceof JSONObject &&
            object.opt("type") instanceof String) {
            entityType = object.optString("type");
            return QuacksLike.ENTITY;
        }
        if (object.opt("list") instanceof JSONArray) {
            array = object.optJSONArray("list");
            return QuacksLike.ENTITIES;
        }
        return QuacksLike.OBJECT;
    }
}
