package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class ExceptionHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final ResponseHandlers responseHandlers;
    private final Resolver resolver;

    public ExceptionHandler(HttpHandler httpHandler, ResponseHandlers responseHandlers, Resolver resolver) {
        this.httpHandler = httpHandler;
        this.responseHandlers = responseHandlers;
        this.resolver = resolver;
    }

    public void handle(Request request, Response response) throws Exception {
        try {
            httpHandler.handle(request, response);
        } catch (InvocationTargetException e) {
            handle(e.getCause(), response);
        } catch (Exception e) {
            handle(e, response);
        }
    }

    private void handle(Throwable value, Response response) throws Exception {
        response.status(Status.INTERNAL_SERVER_ERROR);
        if(response.header(CONTENT_TYPE) == null){
            response.header(CONTENT_TYPE, "text/plain");
        }
        responseHandlers.handle(value, resolver, response);
    }
}
