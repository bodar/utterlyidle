package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Predicates.where;

public class ContentTypePredicate implements Predicate<Response> {
    private final String value;

    private ContentTypePredicate(String value) {
        this.value = value;
    }

    public boolean matches(Response response) {
        String contentType = response.header(HttpHeaders.CONTENT_TYPE);
        return contentType != null && contentType.contains(value);
    }


    public static Predicate<Response> containsContentType(final String value) {
        return new ContentTypePredicate(value);
    }

    public static LogicalPredicate<Pair<Request, Response>> contentType(final String mimeType) {
        return where(second(Response.class), containsContentType(mimeType));
    }


}
