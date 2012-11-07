package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.join;
import static com.googlecode.utterlyidle.handlers.HandlerRule.rule;

public class ResponseHandlers {
    private final List<HandlerRule> guards = new ArrayList<HandlerRule>();
    private final List<HandlerRule> custom = new ArrayList<HandlerRule>();
    private final List<HandlerRule> catchAll = new ArrayList<HandlerRule>();

    public Sequence<HandlerRule> handlers() {
        return join(guards, custom, catchAll);
    }

    public ResponseHandlers addGuard(Predicate<? super Pair<Request, Response>> predicate, Class<? extends ResponseHandler> handler) {
        guards.add(rule(predicate, handler));
        return this;
    }

    public ResponseHandlers addGuard(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler handler) {
        guards.add(rule(predicate, handler));
        return this;
    }

    public ResponseHandlers add(Predicate<? super Pair<Request, Response>> predicate, Class<? extends ResponseHandler> handler) {
        custom.add(rule(predicate, handler));
        return this;
    }

    public ResponseHandlers add(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler handler) {
        custom.add(rule(predicate, handler));
        return this;
    }

    public ResponseHandlers addCatchAll(Predicate<? super Pair<Request, Response>> predicate, Class<? extends ResponseHandler> handler) {
        catchAll.add(rule(predicate, handler));
        return this;
    }

    public ResponseHandlers addCatchAll(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler handler) {
        catchAll.add(rule(predicate, handler));
        return this;
    }
}
