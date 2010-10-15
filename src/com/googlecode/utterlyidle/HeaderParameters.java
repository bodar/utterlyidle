package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;

import javax.servlet.http.HttpServletRequest;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.PathParameters.pairIntoParameters;

public class HeaderParameters extends Parameters {
    public static HeaderParameters headerParameters(Pair<String, String>... pairs) {
        return (HeaderParameters) sequence(pairs).foldLeft(new HeaderParameters(), pairIntoParameters());
    }

    public static HeaderParameters headerParameters(final HttpServletRequest request) {
        return (HeaderParameters) Sequences.<String>sequence(request.getHeaderNames()).foldLeft(new HeaderParameters(), new Callable2<Parameters, String, Parameters>() {
            public Parameters call(Parameters result, final String name) throws Exception {
                return (HeaderParameters) Sequences.<String>sequence(request.getHeaders(name)).foldLeft(result, HeaderParameters.addParameter(name));
            }
        });
    }

    public static Callable2<Parameters, String, Parameters> addParameter(final String name) {
        return new Callable2<Parameters, String, Parameters>() {
            public Parameters call(Parameters result, String value) throws Exception {
                return result.add(name, value);
            }
        };
    }

}