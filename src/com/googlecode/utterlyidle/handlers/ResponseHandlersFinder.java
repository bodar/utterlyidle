package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.modules.DependsOnResolver;
import com.googlecode.yadic.Resolver;

import static com.googlecode.utterlyidle.handlers.HandlerRule.getHandlerFromRule;
import static com.googlecode.utterlyidle.handlers.HandlerRule.matches;
import static com.googlecode.yadic.resolvers.Resolvers.create;
import static com.googlecode.yadic.resolvers.Resolvers.resolve;

public class ResponseHandlersFinder{
    private final ResponseHandlers registry;
    private final Resolver resolver;

    public ResponseHandlersFinder(ResponseHandlers registry, Resolver resolver) {
        this.registry = registry;
        this.resolver = resolver;
    }

    private Option<ResponseHandler> find(final Request request, final Response response){
        return registry.handlers().
                filter(matches(request, response)).
                map(getHandlerFromRule()).
                headOption().
                map(createHandlerIfNeeded()).
                map(injectResolverIfNeeded());
    }

    private Callable1<? super ResponseHandler, ResponseHandler> injectResolverIfNeeded() {
        return new Callable1<ResponseHandler, ResponseHandler>() {
            public ResponseHandler call(ResponseHandler handler) throws Exception {
                if(handler instanceof DependsOnResolver){
                    ((DependsOnResolver) handler).setResolver(resolver);
                }
                return handler;
            }
        };
    }

    private Callable1<Object, ResponseHandler> createHandlerIfNeeded() {
        return new Callable1<Object, ResponseHandler>() {
            public ResponseHandler call(Object handler) throws Exception {
                if (handler instanceof Class) {
                    Class handlerClass = (Class) handler;
                    return (ResponseHandler) resolve(create(handlerClass, resolver), handlerClass);
                }
                return (ResponseHandler) handler;
            }
        };
    }

    public Response findAndHandle(Request request, Response response) throws Exception {
        return find(request, response).get().handle(response);
    }


}
