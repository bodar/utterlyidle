package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Curried2;
import com.googlecode.totallylazy.Uri;

import static com.googlecode.utterlyidle.RequestBuilder.modify;

public interface Request {
    public String method();

    public Uri uri();

    public HeaderParameters headers();

    public Entity entity();

    class functions {
        public static Curried2<Request, Object, Request> replaceHeader(final String name) {
            return new Curried2<Request, Object, Request>() {
                @Override
                public Request call(final Request request, final Object value) throws Exception {
                    return modify(request).replaceHeader(name, value).build();
                }
            };
        }

        public static Function<Request, String> header(final String name) {
            return new Function<Request, String>() {
                @Override
                public String call(final Request request) throws Exception {
                    return request.headers().getValue(name);
                }
            };
        }

        public static Function<Request, Uri> uri() {
            return new Function<Request, Uri>() {
                @Override
                public Uri call(final Request request) throws Exception {
                    return request.uri();
                }
            };
        }
    }
}