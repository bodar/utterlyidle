package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.lang.reflect.InvocationTargetException;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;

public class ExceptionHandler implements HttpHandler {
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
        } catch (Exception e) {
            return findAndHandle(request, e);
        }
    }

    private Response findAndHandle(Request request, Throwable throwable) throws Exception {
        Response response = response(
                INTERNAL_SERVER_ERROR,
                headerParameters(pair(CONTENT_TYPE, TEXT_PLAIN)),
                throwable);
        return handlers.findAndHandle(request, response);
    }
}
