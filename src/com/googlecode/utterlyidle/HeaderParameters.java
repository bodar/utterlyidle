package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.PersistentList;

import java.util.Map;

import static com.googlecode.totallylazy.Pair.functions.pairToString;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;

public class HeaderParameters extends Parameters<HeaderParameters> {
    public HeaderParameters() {
        this(PersistentList.constructors.<Pair<String, String>>empty());
    }

    public HeaderParameters(PersistentList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected HeaderParameters self(PersistentList<Pair<String, String>> values) {
        return new HeaderParameters(values);
    }

    public static HeaderParameters headerParameters() {
        return new HeaderParameters();
    }

    @SafeVarargs
    public static HeaderParameters headerParameters(final Pair<String, String>... pairs) {
        return headerParameters(sequence(pairs));
    }

    public static HeaderParameters headerParameters(final Iterable<? extends Pair<String, String>> pairs) {
        if(pairs instanceof HeaderParameters) return (HeaderParameters) pairs;
        return sequence(pairs).foldLeft(new HeaderParameters(), Parameters.<HeaderParameters>pairIntoParameters());
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
}