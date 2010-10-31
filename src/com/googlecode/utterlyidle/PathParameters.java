package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;

public class PathParameters extends Parameters {
    public static PathParameters pathParameters(Pair<String, String>... pairs) {
        return (PathParameters) sequence(pairs).foldLeft(new PathParameters(), pairIntoParameters());
    }
}