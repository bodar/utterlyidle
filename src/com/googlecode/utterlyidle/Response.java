package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.Cookie;

public interface Response {
    Status status();

    Iterable<String> headers(String name);

    HeaderParameters headers();

    Response header(String name, Object value);

    Response cookie(String name, Cookie value);

    byte[] bytes();

    Response bytes(byte[] value);

    Object entity();

    Response entity(Object value);

    public static class methods{
        private methods() {}

        public static String header(Response response, String name) {
            return response.headers().getValue(name);
        }
    }

}
