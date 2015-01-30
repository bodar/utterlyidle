package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.PersistentList;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class PathParameters extends Parameters<String, String, PathParameters> {
    private PathParameters() {
        this(PersistentList.constructors.<Pair<String, String>>empty());
    }

    private PathParameters(PersistentList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected PathParameters self(PersistentList<Pair<String, String>> values) {
        return new PathParameters(values);
    }

    public static PathParameters pathParameters() {
        return new PathParameters();
    }

    @SafeVarargs
    public static PathParameters pathParameters(Pair<String, String>... pairs) {
        return pathParameters(sequence(pairs));
    }

    public static PathParameters pathParameters(Iterable<Pair<String, String>> pairs) {
        return (PathParameters) sequence(pairs).foldLeft(new PathParameters(), Parameters.<String,String, PathParameters>pairIntoParameters());
    }
}