package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;

import javax.servlet.http.HttpServletRequest;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.FormParameters.addParameters;
import static com.googlecode.utterlyidle.HeaderParameters.addParameter;
import static com.googlecode.utterlyidle.PathParameters.pairIntoParameters;

public class QueryParameters extends Parameters {
    public static QueryParameters queryParameters(Pair<String, String>... pairs) {
        return (QueryParameters) sequence(pairs).foldLeft(new QueryParameters(), pairIntoParameters());
    }

    public static QueryParameters queryParameters(final HttpServletRequest request) {
        return (QueryParameters) Sequences.<String>sequence(request.getParameterNames()).foldLeft(new QueryParameters(), addParameters(request));
    }
}