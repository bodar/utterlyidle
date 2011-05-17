package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;

public class HeaderParameters extends Parameters<String, String> {
    public HeaderParameters() {
        super(equalIgnoringCase());
    }

    public static HeaderParameters headerParameters(Pair<String, String>... pairs) {
        return headerParameters(sequence(pairs));
    }
    public static HeaderParameters headerParameters(Iterable<Pair<String, String>> pairs) {
        return (HeaderParameters) sequence(pairs).foldLeft(new HeaderParameters(), Parameters.<String,String>pairIntoParameters());
    }

    @Override
    public String toString() {
            return sequence(this).map(pairToString("", ": ", "")).toString(HTTP_LINE_SEPARATOR);
    }

    private Callable1<? super Pair<String, String>, String> pairToString(final String start, final String separator, final String end) {
        return new Callable1<Pair<String, String>, String>() {
            public String call(Pair<String, String> pair) throws Exception {
                return pair.toString(start, separator, end);
            }
        };
    }

    public static HeaderParameters withXForwardedFor(ClientAddress clientAddress, HeaderParameters headerParameters) {
        if(!headerParameters.contains("X-Forwarded-For")) {
            headerParameters.add("X-Forwarded-For", clientAddress.value());
        }
        return headerParameters;
    }
}