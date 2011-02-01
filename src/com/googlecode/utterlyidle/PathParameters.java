package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class PathParameters extends Parameters<String, String> {
    public PathParameters() {
        super(equalIgnoringCase());
    }

    public static PathParameters pathParameters(Pair<String, String>... pairs) {
        return pathParameters(sequence(pairs));
    }

    public static PathParameters pathParameters(Iterable<Pair<String, String>> pairs) {
        return (PathParameters) sequence(pairs).foldLeft(new PathParameters(), Parameters.<String,String>pairIntoParameters());
    }
}