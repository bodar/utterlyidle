package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.nio.file.Paths;

import static com.googlecode.utterlyidle.RequestBuilder.modify;

public class NormaliseUriHandler implements HttpHandler {
    private final HttpHandler httpHandler;

    public NormaliseUriHandler(final HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return httpHandler.handle(modify(request).uri(request.uri().normalise()).build());
    }
}
