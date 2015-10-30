package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.UrlEncodedMessage;
import com.googlecode.totallylazy.functions.Compose;
import com.googlecode.totallylazy.functions.Unary;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Entity.DEFAULT_CHARACTER_SET;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_FORM_URLENCODED;
import static com.googlecode.utterlyidle.MemoryRequest.memoryRequest;
import static com.googlecode.utterlyidle.annotations.HttpMethod.*;
import static java.lang.String.format;

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
        static Request request(String method, String uri, Unary<Request>... builders) {
            return request(method, Uri.uri(uri), builders);
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
            return get(Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request get(Uri uri, Unary<Request>... builders) {
            return request(GET, uri, builders);
        }

        @SafeVarargs
        static Request post(String uri, Unary<Request>... builders) {
            return post(Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request post(Uri uri, Unary<Request>... builders) {
            return request(POST, uri, builders);
        }

        @SafeVarargs
        static Request put(String uri, Unary<Request>... builders) {
            return request(PUT, Uri.uri(uri), builders);
        }

        @SafeVarargs
        static Request put(Uri uri, Unary<Request>... builders) {
            return request(PUT, uri, builders);
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
            return header(replace(name, value));
        }

        @SafeVarargs
        static Unary<Request> header(Unary<Parameters<?>>... builders) {
            return request -> modify(request, header(apply(request.headers(), builders)));
        }

        static Unary<Request> header(Iterable<? extends Pair<String,String>> parameters) {
            return request -> request(request.method(), request.uri(), HeaderParameters.headerParameters(parameters), request.entity());
        }

        static Unary<Request> entity(Object value) {
            return request -> request(request.method(), request.uri(), request.headers(), Entity.entity(value));
        }

        static Unary<Request> query(String name, Object value) {
            return query(replace(name, value));
        }

        @SafeVarargs
        static Unary<Request> query(Unary<Parameters<?>>... builders) {
            return request -> {
                QueryParameters parsed = QueryParameters.parse(request.uri().query());
                return modify(request, query(apply(parsed, builders)));
            };
        }

        static Unary<Request> query(Iterable<? extends Pair<String,String>> parameters) {
            return request -> {
                String encoded = UrlEncodedMessage.toString(parameters);
                return modify(request, uri(request.uri().query(encoded)));
            };
        }

        static Unary<Request> form(String name, Object value) {
            return form(replace(name, value));
        }

        @SafeVarargs
        static Unary<Request> form(Unary<Parameters<?>>... builders) {
            return request -> {
                FormParameters parsed = FormParameters.parse(request.entity());
                return modify(request, form(apply(parsed, builders)));
            };
        }

        static Unary<Request> form(Iterable<? extends Pair<String,String>> parameters) {
            return request -> modify(request,
                    header(CONTENT_TYPE, format("%s; charset=%s", APPLICATION_FORM_URLENCODED, DEFAULT_CHARACTER_SET)),
                    entity(UrlEncodedMessage.toString(parameters)));
        }

        static Unary<Request> cookie(String name, Object value) {
            return cookie(replace(name, value));
        }

        @SafeVarargs
        static Unary<Request> cookie(Unary<Parameters<?>>... builders) {
            return request -> {
                CookieParameters parsed = CookieParameters.cookies(request);
                return modify(request, cookie(apply(parsed, builders)));
            };
        }

        static Unary<Request> cookie(Iterable<? extends Pair<String,String>> parameters) {
            return request -> modify(request,
                    header(param(COOKIE, CookieParameters.cookies(parameters).toList())));
        }

        static Unary<Parameters<?>> add(String name, Object value){
            return params -> params.add(name, value.toString());
        }

        static Unary<Parameters<?>> replace(String name, Object value){
            return params -> params.replace(name, value.toString());
        }

        static Unary<Parameters<?>> remove(String name){
            return params -> params.remove(name);
        }

        static Unary<Parameters<?>> param(String name, Object value){
            return params -> params.replace(name, value.toString());
        }

        static Unary<Parameters<?>> param(String name, List<?> values){
            return params -> sequence(values).fold(params.remove(name), (acc, item) -> acc.add(name, item.toString()));
        }

        @SafeVarargs
        static <T> T apply(T seed, Unary<T>... builders) {
            return sequence(builders).reduce(Compose.<T>compose()).apply(seed);
        }
    }
}