package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.join;
import static com.googlecode.utterlyidle.handlers.HandlerRule.rule;

public abstract class HandlerRegistry<T> {
    private final List<HandlerRule> guards = new ArrayList<HandlerRule>();
    private final List<HandlerRule> custom = new ArrayList<HandlerRule>();
    private final List<HandlerRule> catchAll = new ArrayList<HandlerRule>();

    public Sequence<HandlerRule> handlers() {
        return join(guards, custom, catchAll);
    }

    public void addGuard(Predicate<? super Pair<Request, Response>> predicate, Class<? extends T> handler) {
        guards.add(rule(predicate, handler));
    }

    public void addGuard(Predicate<? super Pair<Request, Response>> predicate, T handler) {
        guards.add(rule(predicate, handler));
    }

    public void add(Predicate<? super Pair<Request, Response>> predicate, Class<? extends T> handler) {
        custom.add(rule(predicate, handler));
    }

    public void add(Predicate<? super Pair<Request, Response>> predicate, T handler) {
        custom.add(rule(predicate, handler));
    }

    public void addCatchAll(Predicate<? super Pair<Request, Response>> predicate, Class<? extends T> handler) {
        catchAll.add(rule(predicate, handler));
    }

    public void addCatchAll(Predicate<? super Pair<Request, Response>> predicate, T handler) {
        catchAll.add(rule(predicate, handler));
    }
}
