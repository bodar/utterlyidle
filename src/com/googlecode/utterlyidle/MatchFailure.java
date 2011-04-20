package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;

public class MatchFailure {
    private final Status status;
    private final Sequence<Activator> matchesSoFar;

    public MatchFailure(Status status, Sequence<Activator> matchesSoFar) {
        this.status = status;
        this.matchesSoFar = matchesSoFar;
    }

    public static MatchFailure matchFailure(Status status, Sequence<Activator> matchesSoFar) {
        return new MatchFailure(status, matchesSoFar);
    }

    public Status status() {
        return status;
    }

    public Sequence<Activator> matchesSoFar() {
        return matchesSoFar;
    }

}
