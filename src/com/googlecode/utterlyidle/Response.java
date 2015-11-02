package com.googlecode.utterlyidle;

public interface Response extends HttpMessage<Response> {
    Status status();

    @Override
    default String startLine() {
        return String.format("%s %s", version(), status());
    }

    default Response status(Status value) {
        return create(value, headers(), entity());
    }

    Response create(Status status, HeaderParameters headers, Entity entity);

    @Override
    default Response create(HeaderParameters headers, Entity entity) {
        return create(status(), headers, entity);
    }
}