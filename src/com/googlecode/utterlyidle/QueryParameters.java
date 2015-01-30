package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.UrlEncodedMessage;
import com.googlecode.totallylazy.collections.PersistentList;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class QueryParameters extends Parameters<String, String, QueryParameters> {
    private QueryParameters() {
        this(PersistentList.constructors.<Pair<String, String>>empty());
    }

    private QueryParameters(PersistentList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected QueryParameters self(PersistentList<Pair<String, String>> values) {
        return new QueryParameters(values);
    }

    public static QueryParameters parse(String value) {
        return sequence(UrlEncodedMessage.parse(value)).foldLeft(new QueryParameters(), Parameters.<String, String, QueryParameters>pairIntoParameters());
    }

    public static QueryParameters queryParameters() {
        return new QueryParameters();
    }

    @SafeVarargs
    public static QueryParameters queryParameters(Pair<String, String>... pairs) {
        return queryParameters(sequence(pairs));
    }

    public static QueryParameters queryParameters(Iterable<Pair<String, String>> pairs) {
        return sequence(pairs).foldLeft(new QueryParameters(), Parameters.<String, String, QueryParameters>pairIntoParameters());
    }

    @Override
    public String toString() {
        String value = UrlEncodedMessage.toString(this);
        return value.equals("") ? "" : "?" + value;
    }
}