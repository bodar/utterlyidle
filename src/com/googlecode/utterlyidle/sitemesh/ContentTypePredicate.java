package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.functions.Callables.second;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;

public class ContentTypePredicate implements Predicate<Response> {
    private final String value;

    private ContentTypePredicate(String value) {
        this.value = value;
    }

    public boolean matches(Response response) {
        return response.header(CONTENT_TYPE).exists(Strings.contains(value));
    }

    public static Predicate<Response> containsContentType(final String value) {
        return new ContentTypePredicate(value);
    }

    public static LogicalPredicate<Pair<Request, Response>> contentType(final String mimeType) {
        return where(second(Response.class), containsContentType(mimeType));
    }


}
