package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.one;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.Response.methods.headerOption;

public class Responses {
    public static Response response() {
        return response(Status.OK);
    }

    public static Response response(Status status) {
        return response(status, Sequences.<Pair<String, String>>empty());
    }

    public static Response response(Status status, Iterable<? extends Pair<String, String>> headerParameters) {
        return response(status, headerParameters, null);
    }

    public static Response response(Status status, Iterable<? extends Pair<String, String>> headerParameters, Object entity) {
        return MemoryResponse.memoryResponse(status, headerParameters, entity);
    }

    public static Response seeOther(Uri location) {
        return seeOther(location.toString());
    }

    public static Response seeOther(String location) {
        return response(Status.SEE_OTHER, one(pair(HttpHeaders.LOCATION, location)));
    }

    public static Callable1<Response, Status> status() {
        return new Callable1<Response, Status>() {
            public Status call(Response response) throws Exception {
                return response.status();
            }
        };
    }

    public static LogicalPredicate<Pair<Request, Response>> status(final Status status) {
        return where(second(Response.class), where(Responses.status(), is(status)));
    }

    public static Option<Integer> contentLength(Response response) {
        return headerOption(response, CONTENT_LENGTH).map(asInteger());
    }

    private static Function1<String, Integer> asInteger() {
        return new Function1<String, Integer>() {
            @Override
            public Integer call(String value) throws Exception {
                return Integer.parseInt(value);
            }
        };
    }
}
