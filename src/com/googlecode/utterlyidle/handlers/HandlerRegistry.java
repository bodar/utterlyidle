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

public abstract class HandlerRegistry<T> {
    private final List<Pair<Predicate, Object>> guards = new ArrayList<Pair<Predicate, Object>>();
    private final List<Pair<Predicate, Object>> custom = new ArrayList<Pair<Predicate, Object>>();
    private final List<Pair<Predicate, Object>> catchAll = new ArrayList<Pair<Predicate, Object>>();

    public Sequence<Pair<Predicate, Object>> handlers() {
        return join(guards, custom, catchAll);
    }

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
}
