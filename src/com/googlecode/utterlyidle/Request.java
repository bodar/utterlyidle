package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.functions.Curried2;
import com.googlecode.totallylazy.http.Uri;

import static com.googlecode.utterlyidle.RequestBuilder.modify;

public interface Request {
    String method();

    Uri uri();

    HeaderParameters headers();

    Entity entity();

    class functions {
        public static Curried2<Request, Object, Request> replaceHeader(final String name) {
            return (request, value) -> modify(request).replaceHeader(name, value).build();
        }

        public static Function1<Request, String> header(final String name) {
            return request -> request.headers().getValue(name);
        }

        public static Function1<Request, Uri> uri() {
            return Request::uri;
        }
    }
}