package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.cookies.Cookie;

import java.util.Date;
import java.util.List;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;

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

    @Deprecated
    public ResponseBuilder cookie(String name, Cookie value) {
        return cookie(value);
    }

    public ResponseBuilder cookie(Cookie cookie) {
        return header(HttpHeaders.SET_COOKIE, toHttpHeader(cookie));
    }

    public Response build() {
        return Responses.response(status, HeaderParameters.headerParameters(headers), entity);
    }


    public ResponseBuilder removeHeaders(String name) {
        RequestBuilder.removeHeaders(headers, name);
        return this;
    }

    public ResponseBuilder entity(Object value) {
        entity = Entity.entity(value);
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
