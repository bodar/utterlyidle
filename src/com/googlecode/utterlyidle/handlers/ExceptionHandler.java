package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class ExceptionHandler implements RequestHandler{
    private final RequestHandler requestHandler;
    private final ResponseHandlers responseHandlers;
    private final Resolver resolver;

    public ExceptionHandler(RequestHandler requestHandler, ResponseHandlers responseHandlers, Resolver resolver) {
        this.requestHandler = requestHandler;
        this.responseHandlers = responseHandlers;
        this.resolver = resolver;
    }

    public void handle(Request request, Response response) throws Exception {
        try {
            requestHandler.handle(request, response);
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
