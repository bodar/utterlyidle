package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Strings;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

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
            return sequence(this).map(pairToString("", ": ", "")).toString("\n");
    }

    private Callable1<? super Pair<String, String>, String> pairToString(final String start, final String separator, final String end) {
        return new Callable1<Pair<String, String>, String>() {
            public String call(Pair<String, String> pair) throws Exception {
                return pair.toString(start, separator, end);
            }
        };
    }
}