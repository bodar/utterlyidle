package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Function2;
import com.googlecode.totallylazy.Uri;

import static com.googlecode.utterlyidle.RequestBuilder.modify;

public interface Request {
    public String method();

    public Uri uri();

    public HeaderParameters headers();

    public Entity entity();

    class functions {
        public static Function2<Request, Object, Request> replaceHeader(final String name) {
            return new Function2<Request, Object, Request>() {
                @Override
                public Request call(final Request request, final Object value) throws Exception {
                    return modify(request).replaceHeader(name, value).build();
                }
            };
        }

        public static Function1<Request, String> header(final String name) {
            return new Function1<Request, String>() {
                @Override
                public String call(final Request request) throws Exception {
                    return request.headers().getValue(name);
                }
            };
        }
    }
}