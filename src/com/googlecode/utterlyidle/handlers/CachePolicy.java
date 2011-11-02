package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class CachePolicy implements Value<Integer>, Predicate<Pair<Request, Response>> {
    private final int seconds;
    private final List<Predicate<Pair<Request, Response>>> predicates = new ArrayList<Predicate<Pair<Request, Response>>>();

    @SuppressWarnings("unchecked")
    public CachePolicy(int seconds) {
        this.seconds = seconds;
    }

    public static CachePolicy cachePolicy(final int seconds) {
        return new CachePolicy(seconds);
    }

    public CachePolicy add(Predicate<? super Pair<Request, Response>> predicate) {
        predicates.add((Predicate<Pair<Request, Response>>) predicate);
        return this;
    }

    @Override
    public Integer value() {
        return seconds;
    }

    @Override
    public boolean matches(Pair<Request, Response> pair) {
        return sequence(predicates).exists(Predicates.matches(pair));
    }
}
