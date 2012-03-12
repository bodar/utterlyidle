package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class FormParameters extends Parameters<String,String> {
    public FormParameters() {
        super(equalIgnoringCase());
    }

    public static FormParameters formParameters(Pair<String, String>... pairs) {
        return formParameters(sequence(pairs));
    }

    public static FormParameters formParameters(Iterable<Pair<String, String>> pairs) {
        return (FormParameters) sequence(pairs).foldLeft(new FormParameters(), Parameters.<String,String>pairIntoParameters());
    }

    public static FormParameters parse(String value) {
        return (FormParameters) sequence(UrlEncodedMessage.parse(value)).foldLeft(new FormParameters(), Parameters.<String,String>pairIntoParameters());
    }

    @Override
    public String toString() {
        return UrlEncodedMessage.toString(this);
    }
}