package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.ImmutableList;

import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;

public class HeaderParameters extends Parameters<String, String, HeaderParameters> {
    public HeaderParameters() {
        this(ImmutableList.constructors.<Pair<String, String>>empty());
    }

    public HeaderParameters(ImmutableList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected HeaderParameters self(ImmutableList<Pair<String, String>> values) {
        return new HeaderParameters(values);
    }

    public static HeaderParameters headerParameters() {
        return new HeaderParameters();
    }

    public static HeaderParameters headerParameters(final Pair<String, String>... pairs) {
        return headerParameters(sequence(pairs));
    }

    public static HeaderParameters headerParameters(final Iterable<? extends Pair<String, String>> pairs) {
        return sequence(pairs).foldLeft(new HeaderParameters(), Parameters.<String, String, HeaderParameters>pairIntoParameters());
    }

    public static HeaderParameters headerParameters(final Map<String, ? extends Iterable<String>> requestHeaders) {
        HeaderParameters result = new HeaderParameters();
        for (Map.Entry<String, ? extends Iterable<String>> entry : requestHeaders.entrySet()) {
            for (String value : entry.getValue()) {
                result = result.add(entry.getKey(), value);
            }
        }
        return result;
    }

    @Override
    public HeaderParameters add(String name, String value) {
        if(name == null) {
            return this;
        }
        return super.add(name, value);
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
        if (!headerParameters.contains(X_FORWARDED_FOR)) {
            return headerParameters.add(X_FORWARDED_FOR, clientAddress.value());
        }
        return headerParameters;
    }
}