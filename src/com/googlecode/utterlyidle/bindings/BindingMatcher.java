package com.googlecode.utterlyidle.bindings;

import com.googlecode.totallylazy.Either;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.Request;

public interface BindingMatcher {
    Either<MatchFailure, Binding> match(Request request);
}
