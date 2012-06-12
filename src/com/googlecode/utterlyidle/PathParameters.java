package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.ImmutableList;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class PathParameters extends Parameters<String, String, PathParameters> {
    private PathParameters() {
        this(ImmutableList.constructors.<Pair<String, String>>empty());
    }

    private PathParameters(ImmutableList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected PathParameters self(ImmutableList<Pair<String, String>> values) {
        return new PathParameters(values);
    }

    public static PathParameters pathParameters() {
        return new PathParameters();
    }

    public static PathParameters pathParameters(Pair<String, String>... pairs) {
        return pathParameters(sequence(pairs));
    }

    public static PathParameters pathParameters(Iterable<Pair<String, String>> pairs) {
        return (PathParameters) sequence(pairs).foldLeft(new PathParameters(), Parameters.<String,String, PathParameters>pairIntoParameters());
    }
}