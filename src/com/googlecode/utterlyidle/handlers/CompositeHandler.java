package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Rule;
import com.googlecode.totallylazy.Rules;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import static com.googlecode.totallylazy.Callables.callWith;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.handlers.Handlers.asFunction;

public class CompositeHandler implements HttpHandler {
    private final Rules<Request, Response> rules;

    private CompositeHandler(Rules<Request, Response> rules) {
        this.rules = rules;
    }

    public static CompositeHandler compositeHandler(Rules<Request, Response> rules) {
        return new CompositeHandler(rules);
    }

    public static CompositeHandler compositeHandler() {
        return compositeHandler(Rules.<Request, Response>rules());
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return rules.find(request).
                map(Callables.<Request, Response>callWith(request)).
                getOrElse(response(Status.NOT_FOUND));
    }

    public CompositeHandler add(Predicate<? super Request> predicate, HttpHandler httpHandler) {
        return add(predicate, asFunction(httpHandler));
    }

    public CompositeHandler add(Predicate<? super Request> predicate, Callable1<? super Request, ? extends Response> callable) {
        rules.addFirst(predicate, callable);
        return this;
    }
}
