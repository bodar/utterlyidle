package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.handlers.ContentLengthHandler;


public class MemoryRequest implements Request {
    private final String method;
    private final Uri uri;
    private final Entity entity;
    private final HeaderParameters headers;

    private MemoryRequest(String method, Uri uri, HeaderParameters headers, Entity entity) {
        this.method = method.toUpperCase();
        this.uri = uri;
        this.entity = entity;
        this.headers = headers;
    }

    static Request memoryRequest(String method, Uri uri, HeaderParameters headers, Entity entity) {
        return new MemoryRequest(method, uri, ContentLengthHandler.setContentLength(entity, headers), entity);
    }

    public String method() {
        return method;
    }

    public Uri uri() {
        return uri;
    }

    @Override
    public Request create(String method, Uri uri, HeaderParameters headers, Entity entity) {
        return memoryRequest(method, uri, headers, entity);
    }

    public Entity entity() {
        return entity;
    }

    public HeaderParameters headers() {
        return headers;
    }

    @Override
    public String toString() {
        return HttpMessage.toString(this);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Request && other.toString().equals(toString());
    }
}