package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.utterlyidle.handlers.HandlerRule.handler;
import static com.googlecode.utterlyidle.handlers.HandlerRule.matches;
import static com.googlecode.yadic.CreateCallable.create;

public class ResponseHandlersHandler implements ResponseHandler {
    private final ResponseHandlers registry;
    private final Resolver resolver;

    public ResponseHandlersHandler(ResponseHandlers registry, Resolver resolver) {
        this.registry = registry;
        this.resolver = resolver;
    }

    private ResponseHandler findHandler(final Request request, final Response response){
        final Object handler = registry.handlers().filter(matches(request, response)).map(handler()).head();
        if (handler instanceof Class) {
            return (ResponseHandler) call(create((Class) handler, resolver));
        }
        return (ResponseHandler) handler;
    }

    public void handle(Request request, Response response) throws Exception {
        findHandler(request, response).handle(request, response);
    }
}
