package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.matches;
import static com.googlecode.totallylazy.Sequences.join;
import static com.googlecode.yadic.CreateCallable.create;

public abstract class HandlerRules<T> {
    private final List<Pair<Predicate, Object>> guards = new ArrayList<Pair<Predicate, Object>>();
    private final List<Pair<Predicate, Object>> custom = new ArrayList<Pair<Predicate, Object>>();
    private final List<Pair<Predicate, Object>> catchAll = new ArrayList<Pair<Predicate, Object>>();

    public ResponseHandler<Object> with(final Resolver resolver){
        return new ResponseHandler<Object>() {
            public void handle(Response response) throws Exception {
                final T handler = getHandlerFor(response, resolver);
                process(handler, response);
            }
        };
    }

    private Sequence<Pair<Predicate, Object>> handlers() {
        return join(guards, custom, catchAll);
    }

    public abstract void process(T handler, Response response) throws Exception;

    public void addGuard(Predicate predicate, Class handler) {
        guards.add(pair(predicate, (Object) handler));
    }

    public void addGuard(Predicate predicate, T handler) {
        guards.add(pair(predicate, (Object) handler));
    }

    public void add(Predicate predicate, Class handler) {
        custom.add(pair(predicate, (Object) handler));
    }

    public void add(Predicate predicate, T handler) {
        custom.add(pair(predicate, (Object) handler));
    }

    public void addCatchAll(Predicate predicate, Class handler) {
        catchAll.add(pair(predicate, (Object) handler));
    }

    public void addCatchAll(Predicate predicate, T handler) {
        catchAll.add(pair(predicate, (Object) handler));
    }

    @SuppressWarnings("unchecked")
    private T getHandlerFor(Response response, final Resolver resolver) {
        final Object handler = handlers().filter(by((Callable1) first(), matches(response.entity()))).map(second()).head();
        if (handler instanceof Class) {
            return (T) call(create((Class) handler, resolver));
        }
        return (T) handler;
    }
}
