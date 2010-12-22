package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.modules.DependsOnResolver;
import com.googlecode.yadic.Resolver;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.utterlyidle.handlers.HandlerRule.handler;
import static com.googlecode.utterlyidle.handlers.HandlerRule.matches;
import static com.googlecode.yadic.CreateCallable.create;

public class ResponseHandlersFinder {
    private final ResponseHandlers registry;
    private final Resolver resolver;

    public ResponseHandlersFinder(ResponseHandlers registry, Resolver resolver) {
        this.registry = registry;
        this.resolver = resolver;
    }

    private ResponseHandler lookup(final Request request, final Response response){
        final Object handler = registry.handlers().filter(matches(request, response)).map(handler()).head();
        if (handler instanceof Class) {
            return (ResponseHandler) call(create((Class) handler, resolver));
        }
        return (ResponseHandler) handler;
    }

    public ResponseHandler findHandler(final Request request, final Response response) {
        ResponseHandler handler = lookup(request, response);
        if(handler instanceof DependsOnResolver){
            ((DependsOnResolver) handler).setResolver(resolver);
        }
        return handler;
    }

}
