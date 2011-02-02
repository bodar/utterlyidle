package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;

public interface ActivatorFinder {
    Either<MatchFailure, Activator> findActivator(Request request);
}
