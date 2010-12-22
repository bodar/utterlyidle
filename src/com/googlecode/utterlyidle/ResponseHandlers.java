package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.ResponseHandlerRegistry;
import com.googlecode.yadic.Resolver;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.utterlyidle.handlers.HandlerRule.handler;
import static com.googlecode.utterlyidle.handlers.HandlerRule.matches;
import static com.googlecode.yadic.CreateCallable.create;

public class ResponseHandlers implements ResponseHandler{
    private final ResponseHandlerRegistry registry;
    private final Resolver resolver;

    public ResponseHandlers(ResponseHandlerRegistry registry, Resolver resolver) {
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
