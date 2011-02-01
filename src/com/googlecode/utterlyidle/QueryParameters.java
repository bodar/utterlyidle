package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class QueryParameters extends Parameters<String, String> {
    public QueryParameters() {
        super(equalIgnoringCase());
    }

    public static QueryParameters parse(String value){
         return (QueryParameters) sequence(UrlEncodedMessage.parse(value)).foldLeft(new QueryParameters(), Parameters.<String,String>pairIntoParameters());
    }

    public static QueryParameters queryParameters(Pair<String, String>... pairs) {
        return (QueryParameters) sequence(pairs).foldLeft(new QueryParameters(), Parameters.<String,String>pairIntoParameters());
    }

    @Override
    public String toString() {
        String value = UrlEncodedMessage.toString(this);
        return value.equals("") ? "" : "?" + value;
    }
}