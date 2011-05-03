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

    public static Predicate<HttpSignature> methodMatches(final Request request) {
        return new Predicate<HttpSignature>() {
            public boolean matches(HttpSignature httpSignature) {
                return  new MethodMatcher(httpSignature.httpMethod()).matches(request);
            }
        };
    }
}