package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Unchecked;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.predicates.Predicates;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public abstract class Policy<Self extends Policy<Self>> implements Predicate<Pair<Request, Response>> {
    private final List<Predicate<Pair<Request, Response>>> predicates = new ArrayList<Predicate<Pair<Request, Response>>>();

    protected abstract Self self();

    public Self add(Predicate<? super Pair<Request, Response>> predicate) {
        predicates.add(Unchecked.<Predicate<Pair<Request, Response>>>cast(predicate));
        return self();
    }

    @Override
    public boolean matches(Pair<Request, Response> pair) {
        return sequence(predicates).exists(Predicates.matches(pair));
    }
}
