package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;

import javax.servlet.http.HttpServletRequest;

import static com.googlecode.totallylazy.Sequences.sequence;

public class FormParameters extends Parameters {
    public static FormParameters formParameters(Pair<String, String>... pairs) {
        return (FormParameters) sequence(pairs).foldLeft(new FormParameters(), Parameters.pairIntoParameters());
    }

    public static FormParameters formParameters(final HttpServletRequest request) {
        return (FormParameters) Sequences.<String>sequence(request.getParameterNames()).foldLeft(new FormParameters(), Parameters.<FormParameters>addParameters(request));
    }

}