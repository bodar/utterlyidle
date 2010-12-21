package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.yadic.Resolver;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.matches;
import static com.googlecode.yadic.CreateCallable.create;

public class ResponseHandlerFinder {
    private final ResponseHandlers responseHandlers;
    private final Resolver resolver;

    public ResponseHandlerFinder(ResponseHandlers responseHandlers, Resolver resolver) {
        this.responseHandlers = responseHandlers;
        this.resolver = resolver;
    }

    @SuppressWarnings("unchecked")
    public ResponseHandler findHandler(Request request, Response response){
        final Object handler = responseHandlers.handlers().filter(by((Callable1) first(), matches(response.entity()))).map(second()).head();
        if (handler instanceof Class) {
            return (ResponseHandler) call(create((Class) handler, resolver));
        }
        return (ResponseHandler) handler;
    }
}
