package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.http.Uri;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import static com.googlecode.totallylazy.functions.Callables.second;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.totallylazy.Sequences.one;
import static com.googlecode.totallylazy.numbers.Numbers.greaterThan;

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
        return MemoryResponse.memoryResponse(status, headerParameters, Entity.entity(entity));
    }

    public static Response seeOther(Uri location) {
        return seeOther(location.toString());
    }

    public static Response seeOther(String location) {
        return response(Status.SEE_OTHER, one(pair(HttpHeaders.LOCATION, location)), location);
    }

    public static Function1<Response, Status> status() {
        return Response::status;
    }

    public static LogicalPredicate<Pair<Request, Response>> status(final Status status) {
        return where(second(Response.class), where(Responses.status(), is(status)));
    }
}
