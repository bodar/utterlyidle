package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

import static com.googlecode.utterlyidle.annotations.HttpMethod.ANY;

public class MethodMatcher implements Predicate<Request> {
    private final String method;

    public MethodMatcher(String method) {
        this.method = method;
    }

    public boolean matches(Request request) {
        return method.equalsIgnoreCase(request.method()) || method.equals(ANY);
    }

    public static Predicate<Binding> methodMatches(final Request request) {
        return binding -> new MethodMatcher(binding.httpMethod()).matches(request);
    }
}