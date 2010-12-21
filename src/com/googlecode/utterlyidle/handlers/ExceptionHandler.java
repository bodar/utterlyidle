package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class ExceptionHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final ResponseHandlerFinder handlers;

    public ExceptionHandler(HttpHandler httpHandler, ResponseHandlerFinder handlers) {
        this.httpHandler = httpHandler;
        this.handlers = handlers;
    }

    public void handle(Request request, Response response) throws Exception {
        try {
            httpHandler.handle(request, response);
        } catch (InvocationTargetException e) {
            findAndHandle(request, response.entity(e.getCause()));
        } catch (Exception e) {
            findAndHandle(request, response.entity(e));
        }
    }

    private void findAndHandle(Request request, Response response) throws Exception {
        response.status(Status.INTERNAL_SERVER_ERROR);
        response.header(CONTENT_TYPE, "text/plain");
        handlers.findHandler(request, response).handle(response);
    }
}
