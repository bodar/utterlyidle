package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.UrlEncodedMessage;
import com.googlecode.totallylazy.functions.Compose;
import com.googlecode.totallylazy.functions.Unary;
import com.googlecode.totallylazy.io.Uri;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.MemoryRequest.memoryRequest;
import static com.googlecode.utterlyidle.annotations.HttpMethod.*;

public interface Request {
    String method();

    Uri uri();

    HeaderParameters headers();

    Entity entity();

    interface Builder {
        static Request request(String method, Uri uri, HeaderParameters headers, Entity entity) {
            return memoryRequest(method, uri, headers, entity);
        }

        @SafeVarargs
        static Request request(String method, Uri uri, Unary<Request>... builders) {
            return modify(request(method, uri, headerParameters(), Entity.empty()), builders);
        }

        @SafeVarargs
        static Request modify(Request request, Unary<Request>... builders) {
            return apply(request, builders);
        }

        @SafeVarargs
        static Request get(String uri, Unary<Request>... builders) {
            return request(GET, Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request post(String uri, Unary<Request>... builders) {
            return request(POST, Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request put(String uri, Unary<Request>... builders) {
            return request(PUT, Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request patch(String uri, Unary<Request>... builders) {
            return request(PATCH, Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request delete(String uri, Unary<Request>... builders) {
            return request(DELETE, Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request options(String uri, Unary<Request>... builders) {
            return request(OPTIONS, Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request head(String uri, Unary<Request>... builders) {
            return request(HEAD, Uri.uri(uri), builders);
        }

        static Unary<Request> method(String value) {
            return request -> request(value, request.uri(), request.headers(), request.entity());
        }

        static Unary<Request> uri(String value) {
            return uri(Uri.uri(value));
        }

        static Unary<Request> uri(Uri uri) {
            return request -> request(request.method(), uri, request.headers(), request.entity());
        }

        static Unary<Request> header(String name, Object value) {
            return request -> request(request.method(), request.uri(), request.headers().replace(name, value.toString()), request.entity());
        }

        static Unary<Request> entity(Object value) {
            return request -> request(request.method(), request.uri(), request.headers(), Entity.entity(value));
        }

        static Unary<Request> query(String name, Object value) {
            return query(Parameters.replace(name, value));
        }

        @SafeVarargs
        static Unary<Request> query(Unary<Parameters<String, String, ?>>... builders) {
            return request -> {
                QueryParameters parsed = QueryParameters.parse(request.uri().query());
                return query(apply(parsed, builders)).call(request);
            };
        }

        @SafeVarargs
        static <T> T apply(T seed, Unary<T>... builders) {
            return sequence(builders).reduce(Compose.<T>compose()).apply(seed);
        }

        static Unary<Request> query(Parameters<String, String, ?> parameters) {
            return request -> {
                String encoded = UrlEncodedMessage.toString(parameters);
                return uri(request.uri().query(encoded)).call(request);
            };
        }
    }
}