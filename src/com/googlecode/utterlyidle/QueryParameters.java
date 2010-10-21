package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;

public class QueryParameters extends Parameters {
    public static QueryParameters queryParameters(Pair<String, String>... pairs) {
        return (QueryParameters) sequence(pairs).foldLeft(new QueryParameters(), pairIntoParameters());
    }

}