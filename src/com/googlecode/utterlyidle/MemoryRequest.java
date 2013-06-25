package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;

import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_BODY_SEPARATOR;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;
import static java.lang.String.format;

public class MemoryRequest implements Request {
    private final String method;
    private final Uri uri;
    private final Entity entity;
    private final HeaderParameters headers;

    private MemoryRequest(String method, Uri uri, HeaderParameters headers, Entity entity) {
        this.method = method.toUpperCase();
        this.uri = uri;
        this.entity = entity;
        this.headers = setContentLength(headers);
    }

    static MemoryRequest memoryRequest(String method, Uri uri, HeaderParameters headers, Entity entity) {
        return new MemoryRequest(method, uri, headers, entity);
    }

    private HeaderParameters setContentLength(HeaderParameters headers) {
        if (entity.length() > 0) {
            return headers.remove(CONTENT_LENGTH).add(CONTENT_LENGTH, String.valueOf(entity().length()));
        }
        return headers;
    }

    public String method() {
        return method;
    }

    public Uri uri() {
        return uri;
    }

    public Entity entity() {
        return entity;
    }

    public HeaderParameters headers() {
        return headers;
    }

    @Override
    public String toString() {
        return new StringBuilder(format("%s %s HTTP/1.1%s", method, uri, HTTP_LINE_SEPARATOR)).
                append(headers()).
                append(HTTP_BODY_SEPARATOR).
                append(entity()).
                toString();
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