package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Predicates.always;

public class CachePolicy implements Value<Integer>, Predicate<Pair<Request, Response>> {
    private final int seconds;
    private final Predicate<? super Pair<Request, Response>> predicate;

    public CachePolicy(int seconds, Predicate<? super Pair<Request, Response>> predicate) {
        this.seconds = seconds;
        this.predicate = predicate;
    }

    public static CachePolicy cachePolicy(final int seconds, Predicate<? super Pair<Request, Response>> predicate) {
        return new CachePolicy(seconds, predicate);
    }

    public static CachePolicy cachePolicy(final int seconds) {
        return new CachePolicy(seconds, always());
    }

    @Override
    public Integer value() {
        return seconds;
    }

    @Override
    public boolean matches(Pair<Request, Response> pair) {
        return predicate.matches(pair);
    }
}
