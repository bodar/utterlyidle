package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.cookies.Cookie;

public interface Response {
    Status status();

    HeaderParameters headers();

    Object entity();

    public static class methods{
        private methods() {}

        public static String header(Response response, String name) {
            return response.headers().getValue(name);
        }

        public static Option<String> headerOption(Response response, String name) {
            return response.headers().valueOption(name);
        }

        public static Sequence<String> headers(Response response, String name) {
            return response.headers().getValues(name);
        }
    }
}
