package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;

import javax.servlet.http.HttpServletRequest;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HeaderParameters.addParameter;
import static com.googlecode.utterlyidle.PathParameters.pairIntoParameters;

public class FormParameters extends Parameters {
    public static FormParameters formParameters(Pair<String, String>... pairs) {
        return (FormParameters) sequence(pairs).foldLeft(new PathParameters(), pairIntoParameters());
    }

    public static FormParameters formParameters(final HttpServletRequest request) {
        return (FormParameters) Sequences.<String>sequence(request.getParameterNames()).foldLeft(new FormParameters(), addParameters(request));
    }

    public static Callable2<Parameters, String, Parameters> addParameters(final HttpServletRequest request) {
        return new Callable2<Parameters, String, Parameters>() {
            public Parameters call(Parameters result, final String name) throws Exception {
                return sequence(request.getParameterValues(name)).foldLeft(result, addParameter(name));
            }
        };
    }
}