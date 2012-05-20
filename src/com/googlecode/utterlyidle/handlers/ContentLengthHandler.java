package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class ContentLengthHandler implements HttpHandler {
    private final HttpHandler httpHandler;

    public ContentLengthHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if((!response.entity().isStreaming())) {
            return setContentLength(response, response.entity().asBytes().length);
        }
        return response;
    }

    public static Response setContentLength(Response response, int length) {
        return modify(response).replaceHeaders(CONTENT_LENGTH, length).build();
    }
}