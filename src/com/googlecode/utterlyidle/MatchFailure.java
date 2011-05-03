package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;

public class MatchFailure {
    private final Status status;
    private final Sequence<HttpSignature> matchesSoFar;

    public MatchFailure(Status status, Sequence<HttpSignature> matchesSoFar) {
        this.status = status;
        this.matchesSoFar = matchesSoFar;
    }

    public static MatchFailure matchFailure(Status status, Sequence<HttpSignature> matchesSoFar) {
        return new MatchFailure(status, matchesSoFar);
    }

    public Status status() {
        return status;
    }

    public Sequence<HttpSignature> matchesSoFar() {
        return matchesSoFar;
    }

}
