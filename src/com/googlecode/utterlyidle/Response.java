package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.cookies.Cookie;

public interface Response {
    Status status();

    HeaderParameters headers();

    Response header(String name, Object value);

    Response cookie(String name, Cookie value);

    Object entity();

    Response entity(Object value);

    public static class methods{
        private methods() {}

        public static String header(Response response, String name) {
            return response.headers().getValue(name);
        }

        public static Sequence<String> headers(Response response, String name) {
            return response.headers().getValues(name);
        }
    }
}
