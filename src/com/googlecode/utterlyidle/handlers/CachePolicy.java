package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.totallylazy.Predicates.always;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.util.Arrays.asList;

public class CachePolicy implements Value<Integer>, Predicate<Pair<Request, Response>> {
    private final int seconds;
    private final List<Predicate<? super Pair<Request, Response>>> predicates = new ArrayList<Predicate<? super Pair<Request, Response>>>();

    public CachePolicy(int seconds, Predicate<? super Pair<Request, Response>> predicate) {
        this.seconds = seconds;
        add(predicate);
    }

    public static CachePolicy cachePolicy(final int seconds, Predicate<? super Pair<Request, Response>> predicate) {
        return new CachePolicy(seconds, predicate);
    }

    public static CachePolicy cachePolicy(final int seconds) {
        return new CachePolicy(seconds, always());
    }

    public void add(Predicate<? super Pair<Request, Response>>... predicate) {
        predicates.addAll(asList(predicate));
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
