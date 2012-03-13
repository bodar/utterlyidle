package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.cookies.Cookie;

import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;

public class MemoryResponse implements Response {
    private Status status;
    private final HeaderParameters headers;
    private Object entity;

    private MemoryResponse(Status status, Iterable<? extends Pair<String, String>> headerParameters, Object entity) {
        this.status = status;
        this.headers = headerParameters(headerParameters);
        this.entity = entity == null ? "" : entity;
    }

    static MemoryResponse memoryResponse(final Status status, final Iterable<? extends Pair<String, String>> headerParameters, final Object entity) {
        return new MemoryResponse(status, headerParameters, entity);
    }

    public Status status() {
        return status;
    }

    public HeaderParameters headers() {
        return headers;
    }

    public Object entity() {
        return entity;
    }

    @Override
    public String toString() {
        return String.format("HTTP/1.1 %s\r\n%s\r\n\r\n%s", status, headers, Entity.asString(this));
    }

    @Override
    public int hashCode() {
        return status.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Response) {
            Response response = (Response) other;

            return status.equals(response.status()) && Entity.asString(this).equals(Entity.asString(response)) && headers.equals(response.headers());
        }
        return false;
    }
}