package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.*;

import java.io.IOException;

import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.Response.methods.header;

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
        }
        return response;
    }

    private boolean shouldDecorate(Response response) {
        String header = header(response, CONTENT_TYPE);
        return header != null && header.contains(MediaType.TEXT_HTML);

    }

    private Response decorate(Request request, Response response) throws IOException {
        Decorator decorator = decorators.getDecoratorFor(request, response);
        String result = decorator.decorate(response.entity().toString());
        return ResponseBuilder.modify(response).entity(result).build();
    }
}