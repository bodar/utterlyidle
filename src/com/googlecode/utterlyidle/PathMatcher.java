package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.ResourcePath.resourcePathOf;

public class PathMatcher implements Predicate<Request> {
    private final UriTemplate uriTemplate;

    public PathMatcher(UriTemplate uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public boolean matches(Request request) {
        return uriTemplate.matches(request.uri().path());
    }

    public static Predicate<? super Pair<Request, Response>> path(String path) {
        return where(first(Request.class), new PathMatcher(UriTemplate.uriTemplate(path)));
    }

    public static Predicate<Binding> pathMatches(final Request request) {
        return new Predicate<Binding>() {
            public boolean matches(Binding binding) {
                return new PathMatcher(binding.uriTemplate()).matches(request);
            }
        };
    }
}