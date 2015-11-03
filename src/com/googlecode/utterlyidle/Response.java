package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import javafx.animation.Animation;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.one;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.HttpHeaders.SET_COOKIE;
import static com.googlecode.utterlyidle.Parameters.Builder.param;
import static com.googlecode.utterlyidle.Parameters.Builder.replace;
import static com.googlecode.utterlyidle.Status.*;

public interface Response extends HttpMessage<Response> {
    Status status();

    @Override
    default String startLine() {
        return String.format("%s %s", version(), status());
    }

    default Response status(Status value) {
        return create(value, headers(), entity());
    }

    Response create(Status status, HeaderParameters headers, Entity entity);

    @Override
    default Response create(HeaderParameters headers, Entity entity) {
        return create(status(), headers, entity);
    }

    @Override
    default Response cookie(Cookie cookie) {
        return cookies(cookies().replace(cookie));
    }

    @Override
    default CookieParameters cookies() {
        return CookieParameters.cookies(this);
    }

    @Override
    default Response cookies(Iterable<? extends Pair<String, String>> parameters) {
        return modify(this, HttpMessage.Builder.header(param(SET_COOKIE, CookieParameters.cookies(parameters).toList())));
    }

    static Response response(Status status) {
        return response(status, headerParameters());
    }

    static Response response(Status status, Iterable<? extends Pair<String, String>> headerParameters) {
        return response(status, headerParameters, Entity.empty());
    }

    static Response response(Status status, Iterable<? extends Pair<String, String>> headerParameters, Entity entity) {
        return MemoryResponse.memoryResponse(status, headerParameters, entity);
    }

    static Response ok() {
        return response(OK);
    }

    static Response created(String location) {
        return response(CREATED, one(pair(LOCATION, location)));
    }

    static Response created(Uri location) {
        return created(location.toString());
    }

    static Response accepted() {
        return response(ACCEPTED);
    }

    static Response seeOther(Uri location) {
        return seeOther(location.toString());
    }

    static Response seeOther(String location) {
        return response(SEE_OTHER, one(pair(HttpHeaders.LOCATION, location)), Entity.entity(location));
    }

}