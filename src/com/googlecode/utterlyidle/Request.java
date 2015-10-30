package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Unary;
import com.googlecode.totallylazy.io.Uri;

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
            return request(method, uri, headerParameters(), Entity.empty());
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

    }
}