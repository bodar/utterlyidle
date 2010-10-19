package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;

public class HeaderParameters extends Parameters {
    public static HeaderParameters headerParameters(Pair<String, String>... pairs) {
        return (HeaderParameters) sequence(pairs).foldLeft(new HeaderParameters(), pairIntoParameters());
    }

}