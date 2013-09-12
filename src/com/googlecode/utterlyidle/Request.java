package com.googlecode.utterlyidle;

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
    }
}