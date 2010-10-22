package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.yadic.Resolver;

public class MatchFailure {
    private final Status status;
    private final Sequence<HttpMethodActivator> matchesSoFar;

    public MatchFailure(Status status, Sequence<HttpMethodActivator> matchesSoFar) {
        this.status = status;
        this.matchesSoFar = matchesSoFar;
    }

    public static MatchFailure matchFailure(Status status, Sequence<HttpMethodActivator> matchesSoFar) {
        return new MatchFailure(status, matchesSoFar);
    }

    public Status status() {
        return status;
    }

    public Sequence<HttpMethodActivator> matchesSoFar() {
        return matchesSoFar;
    }

}
