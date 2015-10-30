package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import static com.googlecode.totallylazy.Pair.pair;

public class HandlerRule {
    private final Predicate<? super Pair<Request, Response>> predicate;
    private final Object handler;

    private HandlerRule(Predicate<? super Pair<Request, Response>> predicate, Object handler) {
        this.predicate = predicate;
        this.handler = handler;
    }

    public static HandlerRule rule(Predicate<? super Pair<Request, Response>> predicate, Object handler) {
        return new HandlerRule(predicate, handler);
    }

    public static Predicate<HandlerRule> matches(final Request request, final Response response) {
        return handlerRule -> handlerRule.predicate.matches(pair(request, response));
    }

    public static Function1<? super HandlerRule, Object> getHandlerFromRule() {
        return handlerRule -> handlerRule.handler;
    }

    public static Function1<? super Pair<Request, Response>, Object> entity() {
        return entity(Object.class);
    }

    public static Function1<? super Pair<Request, Response>, Status> status() {
        return pair -> pair.second().status();
    }

    public static Function1<Pair<Request, Response>, String> method() {
        return pair -> pair.first().method();
    }

    public static <T> Function1<? super Pair<Request, Response>, T> entity(final Class<T> aClass) {
        return pair -> {
            if (pair.second().entity().value() != null && !aClass.isAssignableFrom(pair.second().entity().value().getClass())) {
                return null;
            }
            return aClass.cast(pair.second().entity().value());
        };
    }

}
