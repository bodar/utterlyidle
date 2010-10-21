package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;

public class FormParameters extends Parameters {
    public static FormParameters formParameters(Pair<String, String>... pairs) {
        return (FormParameters) sequence(pairs).foldLeft(new FormParameters(), Parameters.pairIntoParameters());
    }


}