package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

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
        return new Predicate<HandlerRule>() {
            public boolean matches(HandlerRule handlerRule) {
                return handlerRule.predicate.matches(pair(request, response));
            }
        };
    }

    public static Callable1<? super HandlerRule, Object> handler() {
        return new Callable1<HandlerRule, Object>() {
            public Object call(HandlerRule handlerRule) throws Exception {
                return handlerRule.handler;
            }
        };
    }

    public static Callable1<? super Pair<Request, Response>, Object> entity() {
        return entity(Object.class);
    }

    public static <T> Callable1<? super Pair<Request, Response>, T> entity(final Class<T> aClass) {
        return new Callable1<Pair<Request, Response>, T>() {
            public T call(Pair<Request, Response> pair) throws Exception {
                return aClass.cast(pair.second().entity());
            }
        };
    }

}
