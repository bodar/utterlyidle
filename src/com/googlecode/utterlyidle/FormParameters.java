package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;

public class FormParameters extends Parameters {
    public static FormParameters parse(String value) {
        return (FormParameters) sequence(UrlEncodedMessage.parse(value)).foldLeft(new FormParameters(), pairIntoParameters());
    }

    public static FormParameters formParameters(Pair<String, String>... pairs) {
        return (FormParameters) sequence(pairs).foldLeft(new FormParameters(), Parameters.pairIntoParameters());
    }

    @Override
    public String toString() {
        return UrlEncodedMessage.toString(this);
    }
}