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

    @Override
    public Response create(Status status, HeaderParameters headers, Entity entity) {
        return memoryResponse(status, headers, entity);
    }

    public HeaderParameters headers() {
        return headers;
    }

    public Entity entity() {
        return entity;
    }

    @Override
    public String toString() {
        return methods.asString(this);
    }

    @Override
    public int hashCode() {
        return status.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Response) {
            Response response = (Response) other;
            return status.equals(response.status()) && entity().toString().equals(response.entity().toString()) && headers.equals(response.headers());
        }
        return false;
    }
}