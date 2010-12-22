package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.handlers.ResponseHandlerRegistry;
import com.googlecode.yadic.Resolver;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.matches;
import static com.googlecode.yadic.CreateCallable.create;

public class ResponseHandlers implements ResponseHandler{
    private final ResponseHandlerRegistry registry;
    private final Resolver resolver;

    public ResponseHandlers(ResponseHandlerRegistry registry, Resolver resolver) {
        this.registry = registry;
        this.resolver = resolver;
    }

    @SuppressWarnings("unchecked")
    private ResponseHandler findHandler(Request request, Response response){
        final Object handler = registry.handlers().filter(by((Callable1) first(), matches(response.entity()))).map(second()).head();
        if (handler instanceof Class) {
            return (ResponseHandler) call(create((Class) handler, resolver));
        }
        return (ResponseHandler) handler;
    }

    public void handle(Request request, Response response) throws Exception {
        findHandler(request, response).handle(request, response);
    }
}
