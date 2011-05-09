package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

public class MethodMatcher implements Predicate<Request> {
    private final String method;

    public MethodMatcher(String method) {
        this.method = method;
    }

    public boolean matches(Request request) {
        return method.equals(request.method());
    }
}