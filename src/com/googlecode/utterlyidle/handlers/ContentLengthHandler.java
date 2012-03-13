package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;

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
        Object entity = response.entity();
        if((entity instanceof byte[])) {
            return setContentLength(response, ((byte[]) entity).length);
        } else if(entity instanceof  String) {
            return setContentLength(response, ((String) entity).getBytes("UTF-8").length);
        }
        return response;
    }

    private Response setContentLength(Response response, int length) {
        return modify(response).replaceHeaders(CONTENT_LENGTH, length).build();
    }
}