package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import static com.googlecode.totallylazy.functions.Callables.first;
import static com.googlecode.totallylazy.Predicates.where;

public class PathMatcher extends LogicalPredicate<Request> {
    private final UriTemplate uriTemplate;

    private PathMatcher(UriTemplate uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public static PathMatcher pathMatches(UriTemplate uriTemplate) {
        return new PathMatcher(uriTemplate);
    }

    public boolean matches(Request request) {
        return uriTemplate.matches(request.uri().path());
    }

    public static LogicalPredicate<? super Pair<Request, Response>> path(String path) {
        return where(first(Request.class), pathMatches(path));
    }

    public static PathMatcher pathMatches(String path) {
        return pathMatches(UriTemplate.uriTemplate(path));
    }

    public static LogicalPredicate<Binding> pathMatches(final Request request) {
        return new LogicalPredicate<Binding>() {
            public boolean matches(Binding binding) {
                return pathMatches(binding.uriTemplate()).matches(request);
            }
        };
    }
}