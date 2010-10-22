package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class QueryParameters extends Parameters {
    public static QueryParameters queryParameters(Pair<String, String>... pairs) {
        return (QueryParameters) sequence(pairs).foldLeft(new QueryParameters(), pairIntoParameters());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Pair<String, List<String>> pair : this) {
            for (String value : pair.second()) {
                if(first) {
                    first = false;
                } else {
                    builder.append("&");
                }
                builder.append(encode(pair.first())).append("=").append(encode(value));
            }
        }
        return "?" + builder.toString();
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}