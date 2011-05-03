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

    public static Predicate<Binding> methodMatches(final Request request) {
        return new Predicate<Binding>() {
            public boolean matches(Binding binding) {
                return  new MethodMatcher(binding.httpMethod()).matches(request);
            }
        };
    }
}