package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;

public class ExceptionHandler implements RequestHandler{
    private final RequestHandler requestHandler;
    private final RendererHandler renderers;
    private final Resolver resolver;

    public ExceptionHandler(RequestHandler requestHandler, RendererHandler renderers, Resolver resolver) {
        this.requestHandler = requestHandler;
        this.renderers = renderers;
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
        response.code(Status.INTERNAL_SERVER_ERROR);
        renderers.handle(value, resolver, response);
    }
}
