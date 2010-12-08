package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

public class PathMatcher implements Predicate<Request> {
    private final UriTemplate uriTemplate;

    public PathMatcher(UriTemplate uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public boolean matches(Request request) {
        return uriTemplate.matches(removeLeadingSlash(request.resourcePath().toString()));
    }

    private String removeLeadingSlash(String path) {
        if(path.startsWith("/")){
            return path.substring(1);
        }
        return path;
    }
}