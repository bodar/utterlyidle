package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.UrlEncodedMessage;
import com.googlecode.totallylazy.collections.PersistentList;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class FormParameters extends Parameters<String,String, FormParameters > {
    private FormParameters() {
        this(PersistentList.constructors.<Pair<String, String>>empty());
    }

    private FormParameters(PersistentList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected FormParameters self(PersistentList<Pair<String, String>> values) {
        return new FormParameters(values);
    }

    public static FormParameters formParameters() {
        return new FormParameters();
    }

    @SafeVarargs
    public static FormParameters formParameters(Pair<String, String>... pairs) {
        return formParameters(sequence(pairs));
    }

    public static FormParameters formParameters(Iterable<Pair<String, String>> pairs) {
        return sequence(pairs).foldLeft(new FormParameters(), Parameters.<String,String, FormParameters>pairIntoParameters());
    }

    public static FormParameters parse(String value) {
        return sequence(UrlEncodedMessage.parse(value)).foldLeft(new FormParameters(), Parameters.<String,String, FormParameters>pairIntoParameters());
    }

    public static FormParameters parse(Entity value) {
        return parse(value.toString());
    }

    @Override
    public String toString() {
        return UrlEncodedMessage.toString(this);
    }
}