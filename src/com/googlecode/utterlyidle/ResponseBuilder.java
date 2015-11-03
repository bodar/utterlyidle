package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.cookies.Cookie;

import java.util.Date;
import java.util.List;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.Strings.startsWith;
import static com.googlecode.totallylazy.functions.Callables.first;
import static com.googlecode.totallylazy.functions.Callables.second;
import static com.googlecode.totallylazy.predicates.Predicates.and;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.predicates.Predicates.where;

@Deprecated
@SuppressWarnings("deprecation")
public class ResponseBuilder {
    private Status status;
    private List<Pair<String, String>> headers;
    private Entity entity;

    public ResponseBuilder(Status status, Iterable<Pair<String, String>> headers, Entity entity) {
        this.status = status;
        this.headers = sequence(headers).toList();
        this.entity = entity;
    }

    public static ResponseBuilder response(Status status) {
        return new ResponseBuilder(status, Sequences.<Pair<String, String>>empty(), Entity.empty());
    }

    public static ResponseBuilder response() {
        return new ResponseBuilder(Status.OK, Sequences.<Pair<String, String>>empty(), Entity.empty());
    }

    public static ResponseBuilder modify(Response response) {
        return new ResponseBuilder(response.status(), response.headers(), response.entity());
    }

    public ResponseBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public ResponseBuilder header(String name, Object value) {
        if (value == null) return this;
        if (value instanceof Date) return header(name, Dates.RFC822().format((Date) value));
        headers.add(pair(name, value.toString()));
        return this;
    }

    public ResponseBuilder cookie(Cookie cookie) {
        return header(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public ResponseBuilder removeCookie(String name) {
        return removeHeaders(HttpHeaders.SET_COOKIE, startsWith(name + "=")).
            cookie(Cookie.expire(name));
    }

    public ResponseBuilder replaceCookie(Cookie cookie) {
        return removeHeaders(HttpHeaders.SET_COOKIE, startsWith(cookie.name() + "=")).cookie(cookie);
    }

    public Response build() {
        return Response.response(status, HeaderParameters.headerParameters(headers), Entity.entity(entity));
    }


    public ResponseBuilder removeHeaders(String name) {
        for (Pair<String, String> header : sequence(headers).filter(where(first(String.class), equalIgnoringCase(name))).realise()) {
            headers.remove(header);
        }
        return this;
    }

    public ResponseBuilder removeHeaders(String name, Predicate<String> valuePredicate) {
        Predicate<Pair<String, String>> nameP = where(first(String.class), is(name));
        Predicate<Pair<String, String>> valueP = where(second(String.class), valuePredicate);
        headers = sequence(headers).filter(not(and(nameP, valueP))).toList();
        return this;
    }

    public ResponseBuilder entity(Object value) {
        if(value instanceof Entity) return entity((Entity) value);
        return entity(Entity.entity(value));
    }

    public ResponseBuilder entity(Entity value) {
        entity = value;
        return this;
    }

    public ResponseBuilder removeEntity() {
        entity = Entity.empty();
        return this;
    }

    public ResponseBuilder replaceHeaders(String name, Object value) {
        return removeHeaders(name).header(name, value);
    }

    public ResponseBuilder contentType(String contentType) {
        return header(HttpHeaders.CONTENT_TYPE, contentType);
    }
}
