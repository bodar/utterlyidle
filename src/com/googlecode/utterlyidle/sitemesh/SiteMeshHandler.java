package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SiteMeshHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final Decorators decorators;

    public SiteMeshHandler(final HttpHandler httpHandler, final Decorators decorators) {
        this.httpHandler = httpHandler;
        this.decorators = decorators;
    }

    public Response handle(final Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if (shouldDecorate(response)) {
            return decorate(request, response);
        } else {
            return response;
        }
    }

    private boolean shouldDecorate(Response response) {
        String header = response.header(HttpHeaders.CONTENT_TYPE);
        return header != null && header.contains(MediaType.TEXT_HTML);

    }

    private Response decorate(Request request, Response response) throws IOException {
        Decorator decorator = decorators.getDecoratorFor(request, response);
        String result = decorator.decorate(Strings.toString(new ByteArrayInputStream(response.bytes())));
        return response.bytes(result.getBytes());
    }
}