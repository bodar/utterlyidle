package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;

public class Responses {
    public static Response response() {
        return response(Status.OK);
    }

    public static Response response(Status status) {
        return new MemoryResponse(status);
    }

    public static Response response(Status status, HeaderParameters headerParameters) {
        return new MemoryResponse(status, headerParameters);
    }

    public static Response seeOther(Uri location) {
        return seeOther(location.toString());
    }

    public static Response seeOther(String location) {
        return response(Status.SEE_OTHER).header(HttpHeaders.LOCATION, location);
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

}
