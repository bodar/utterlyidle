package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;

public class MatchFailure {
    private final Status status;
    private final Sequence<Binding> matchesSoFar;

    public MatchFailure(Status status, Sequence<Binding> matchesSoFar) {
        this.status = status;
        this.matchesSoFar = matchesSoFar;
    }

    public static MatchFailure matchFailure(Status status, Sequence<Binding> matchesSoFar) {
        return new MatchFailure(status, matchesSoFar);
    }

    public Status status() {
        return status;
    }

    public Sequence<Binding> matchesSoFar() {
        return matchesSoFar;
    }

}
