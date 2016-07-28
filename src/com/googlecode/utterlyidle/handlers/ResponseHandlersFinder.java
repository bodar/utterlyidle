package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Container;

import static com.googlecode.utterlyidle.handlers.HandlerRule.getHandlerFromRule;
import static com.googlecode.utterlyidle.handlers.HandlerRule.matches;
import static com.googlecode.yadic.resolvers.Resolvers.create;
import static com.googlecode.yadic.resolvers.Resolvers.resolve;

public class ResponseHandlersFinder{
    private final ResponseHandlers registry;
    private final Container container;

    public ResponseHandlersFinder(ResponseHandlers registry, Container container) {
        this.registry = registry;
        this.container = container;
    }

    private ResponseHandler find(final Request request, final Response response){
        return registry.handlers().
                filter(matches(request, response)).
                map(getHandlerFromRule()).
                headOption().
                map(createHandlerIfNeeded()).
                get();
    }

    private Function1<Object, ResponseHandler> createHandlerIfNeeded() {
        return handler -> {
            if (handler instanceof Class) {
                Class handlerClass = (Class) handler;
                return (ResponseHandler) resolve(create(handlerClass, container), handlerClass);
            }
            return (ResponseHandler) handler;
        };
    }

    public Response findAndHandle(Request request, Response response) throws Exception {
        ResponseHandler responseHandler = find(request, response);
        return responseHandler.handle(response);
    }


}
