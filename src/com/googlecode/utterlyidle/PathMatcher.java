package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Predicates.where;

public class PathMatcher implements Predicate<Request> {
    private final UriTemplate uriTemplate;

    public PathMatcher(UriTemplate uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public boolean matches(Request request) {
        return uriTemplate.matches(request.resourcePath().toString());
    }

    public static Predicate<? super Pair<Request, Response>> path(String path) {
        return where(first(Request.class), new PathMatcher(UriTemplate.uriTemplate(path)));
    }

}