package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;

import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_ENCODING;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.handlers.GzipHandler.clientAcceptsGZip;
import static com.googlecode.utterlyidle.handlers.GzipHandler.isGZipped;
import static com.googlecode.utterlyidle.handlers.GzipHandler.ungzip;

public interface Response {
    Status status();

    HeaderParameters headers();

    Entity entity();

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

        public static String asString(Response response) {
            return String.format("HTTP/1.1 %s\r\n%s\r\n\r\n%s", response.status(), response.headers(), response.entity().toString());
        }
    }
}
