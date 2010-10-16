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
        return (HeaderParameters) Sequences.<String>sequence(request.getHeaderNames()).foldLeft(new HeaderParameters(), new Callable2<HeaderParameters, String, HeaderParameters>() {
            public HeaderParameters call(HeaderParameters result, final String name) throws Exception {
                return (HeaderParameters) Sequences.<String>sequence(request.getHeaders(name)).foldLeft(result, Parameters.<HeaderParameters>addParameter(name));
            }
        });
    }
}