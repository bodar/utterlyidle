package com.googlecode.utterlyidle;

public interface HttpHandler {
    Response handle(final Request request) throws Exception;

    default Response handle(final RequestBuilder builder) throws Exception {
        return handle(builder.build());
    }
}
