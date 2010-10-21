package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.RestEngine;
import com.googlecode.yadic.Resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.matches;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.yadic.CreateCallable.create;

public class ResponseHandlers extends CompositeHandler<ResponseHandler> {
    @Override
    public void process(ResponseHandler handler, Object result, Resolver resolver, Response response) throws IOException {
        handler.handle(result, resolver, response);
    }
}
