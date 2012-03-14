package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;

public class MemoryResponse implements Response {
    private Status status;
    private final HeaderParameters headers;
    private Entity entity;

    private MemoryResponse(Status status, Iterable<? extends Pair<String, String>> headerParameters, Entity entity) {
        this.status = status;
        this.headers = headerParameters(headerParameters);
        this.entity = entity;
    }

    static MemoryResponse memoryResponse(final Status status, final Iterable<? extends Pair<String, String>> headerParameters, final Entity entity) {
        return new MemoryResponse(status, headerParameters, entity);
    }

    public Status status() {
        return status;
    }

    public HeaderParameters headers() {
        return headers;
    }

    public Entity entity() {
        return entity;
    }

    @Override
    public String toString() {
        return String.format("HTTP/1.1 %s\r\n%s\r\n\r\n%s", status, headers, entity().asString());
    }

    @Override
    public int hashCode() {
        return status.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Response) {
            Response response = (Response) other;
            return status.equals(response.status()) && entity().asString().equals(response.entity().asString()) && headers.equals(response.headers());
        }
        return false;
    }
}