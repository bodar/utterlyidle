package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Curried2;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.ResponseBuilder.response;

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
            return String.format("HTTP/1.1 %s%s\r\n\r\n%s", response.status(), pad(response.headers()), response.entity().toString());
        }

        private static String pad(final HeaderParameters headers) {
            if (headers.size() == 0) {
                return Strings.EMPTY;
            }
            return "\r\n" + headers;
        }
    }

    public static class functions {
        public static Function<Object, Response> asResponse() {
            return asResponse(response());
        }

        public static Function<Object, Response> asResponse(final String contentType) {
            return asResponse(response().contentType(contentType));
        }

        public static Function<Object, Response> asResponse(final ResponseBuilder response) {
            return new Function<Object, Response>() {
                @Override
                public Response call(Object entity) throws Exception {
                    return response.entity(entity).build();
                }
            };
        }

        public static Function<Response, Entity> entity() {
            return new Function<Response, Entity>() {
                @Override
                public Entity call(Response response) throws Exception {
                    return response.entity();
                }
            };
        }

        public static LogicalPredicate<Response> entityTypeIs(final Class type) {
            return new LogicalPredicate<Response>() {
                @Override
                public boolean matches(Response other) {
                    return other != null && type.isInstance(other.entity().value());
                }
            };
        }

        public static Function<Response, Status> status() {
            return new Function<Response, Status>() {
                @Override
                public Status call(Response response) throws Exception {
                    return response.status();
                }
            };
        }

        public static Function<Response, String> header(final String name) {
            return new Function<Response, String>() {
                @Override
                public String call(Response response) throws Exception {
                    return response.headers().getValue(name);
                }
            };
        }

        public static Curried2<Response, Object, Response> replaceHeader(final String name) {
            return new Curried2<Response, Object, Response>() {
                @Override
                public Response call(final Response response, final Object value) throws Exception {
                    return modify(response).replaceHeaders(name, value).build();
                }
            };
        }
    }
}