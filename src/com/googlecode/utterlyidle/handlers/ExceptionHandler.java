package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;

import java.lang.reflect.InvocationTargetException;

import static com.googlecode.totallylazy.Debug.debugging;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;

public class ExceptionHandler implements HttpHandler {
    private static final String LOG_PROPERTY = "com.googlecode.utterlyidle.exceptions.log";

    private final HttpHandler httpHandler;
    private final ResponseHandlersFinder handlers;

    public ExceptionHandler(HttpHandler httpHandler, ResponseHandlersFinder handlers) {
        this.httpHandler = httpHandler;
        this.handlers = handlers;
    }

    public Response handle(Request request) throws Exception {
        try {
            return httpHandler.handle(request);
        } catch (InvocationTargetException e) {
            return findAndHandle(request, e.getCause());
        } catch (LazyException e) {
            return findAndHandle(request, e.getCause());
        } catch (Throwable e) {
            return findAndHandle(request, e);
        }
    }

    private Response findAndHandle(Request request, Throwable throwable) {
        if (debugging() && shouldLogExceptions()) throwable.printStackTrace();
        ResponseBuilder response = ResponseBuilder.response(INTERNAL_SERVER_ERROR).
                contentType(TEXT_PLAIN).
                entity(throwable);
        try {
            return handlers.findAndHandle(request, response.build());
        } catch (Throwable t) {
            return response.entity(ExceptionRenderer.toString(t)).build();
        }
    }

    private boolean shouldLogExceptions() {
        String logValue = System.getProperty(LOG_PROPERTY);
        return logValue == null || Boolean.parseBoolean(logValue);
    }
}
