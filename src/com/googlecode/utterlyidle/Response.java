package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.HttpHeaders.SET_COOKIE;
import static com.googlecode.utterlyidle.Parameters.Builder.param;
import static com.googlecode.utterlyidle.Parameters.Builder.replace;

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
}