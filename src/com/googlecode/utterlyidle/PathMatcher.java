package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.ResourcePath.resourcePathOf;

public class PathMatcher implements Predicate<Request> {
    private final BasePath basePath;
    private final UriTemplate uriTemplate;

    public PathMatcher(BasePath basePath, UriTemplate uriTemplate) {
        this.basePath = basePath;
        this.uriTemplate = uriTemplate;
    }

    public boolean matches(Request request) {
        return uriTemplate.matches(resourcePathOf(request, basePath).toString());
    }

    public static Predicate<? super Pair<Request, Response>> path(BasePath basePath, String path) {
        return where(first(Request.class), new PathMatcher(basePath, UriTemplate.uriTemplate(path)));
    }

    public static Predicate<HttpSignature> pathMatches(final BasePath basePath, final Request request) {
        return new Predicate<HttpSignature>() {
            public boolean matches(HttpSignature httpSignature) {
                return new PathMatcher(basePath, httpSignature.uriTemplate()).matches(request);
            }
        };
    }
}