package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

public class PathMatcher implements Predicate<Request> {
    private final UriTemplate uriTemplate;

    public PathMatcher(UriTemplate uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    private String removeLeadingSlash(String path) {
        return path.replaceFirst("^/", "");
    }

    public boolean matches(Request request) {
        return uriTemplate.matches(removeLeadingSlash(request.url().path().toString()));
    }
}