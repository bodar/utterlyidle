package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;

public class FormParameters extends Parameters {
    public static FormParameters formParameters(Pair<String, String>... pairs) {
        return formParameters(sequence(pairs));
    }

    public static FormParameters formParameters(Iterable<Pair<String, String>> pairs) {
        return (FormParameters) sequence(pairs).foldLeft(new FormParameters(), Parameters.pairIntoParameters());
    }

    public static FormParameters parse(String value) {
        return (FormParameters) sequence(UrlEncodedMessage.parse(value)).foldLeft(new FormParameters(), pairIntoParameters());
    }

    @Override
    public String toString() {
        return UrlEncodedMessage.toString(this);
    }
}