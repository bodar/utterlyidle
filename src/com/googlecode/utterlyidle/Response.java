package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

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
            return String.format("HTTP/1.1 %s\r\n%s\r\n\r\n%s", response.status(), response.headers(), response.entity().toString());
        }
    }

    public static class functions {
        public static Function1<Object, Response> asResponse() {
            return asResponse(response());
        }

        public static Function1<Object, Response> asResponse(final String contentType) {
            return asResponse(response().contentType(contentType));
        }

        public static Function1<Object, Response> asResponse(final ResponseBuilder response) {
            return new Function1<Object, Response>() {
                @Override
                public Response call(Object entity) throws Exception {
                    return response.entity(entity).build();
                }
            };
        }

        public static Function1<Response, Entity> entity() {
            return new Function1<Response, Entity>() {
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

        public static Function1<Response, Status> status() {
            return new Function1<Response, Status>() {
                @Override
                public Status call(Response response) throws Exception {
                    return response.status();
                }
            };
        }

        public static Function1<Response, String> header(final String name) {
            return new Function1<Response, String>() {
                @Override
                public String call(Response response) throws Exception {
                    return response.headers().getValue(name);
                }
            };
        }
    }
}