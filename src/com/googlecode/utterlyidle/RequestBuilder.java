package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.cookies.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.Requests.request;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;
import static java.lang.String.format;

public class RequestBuilder implements Callable<Request> {
    private final String method;
    private Uri uri;
    private final List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
    private byte[] entity = new byte[0];

    public RequestBuilder(String method, Uri uri) {
        this.method = method;
        this.uri = uri;
    }

    public RequestBuilder(String method, String uri) {
        this(method, Uri.uri(uri));
    }

    public static RequestBuilder modify(Request request) {
        return new RequestBuilder(request);
    }

    public RequestBuilder(Request request) {
        this(request.method(), request.uri());

        sequence(request.headers()).fold(this, new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> nameAndValue) throws Exception {
                requestBuilder.withHeader(nameAndValue.first(), nameAndValue.second());
                return requestBuilder;
            }
        });
        this.entity = request.entity();
    }

    public RequestBuilder withUri(Uri value) {
        uri = value;
        return this;
    }

    public RequestBuilder accepting(String value) {
        return withHeader(HttpHeaders.ACCEPT, value);
    }

    public RequestBuilder header(String name, Object value) {
        if (value == null) {
            return this;
        }

        headers.add(pair(name, value.toString()));
        return this;
    }

    public RequestBuilder withHeader(String name, Object value) {
        return header(name, value);
    }

    public RequestBuilder withCookie(String name, Cookie cookie) {
        return cookie(name, cookie);
    }

    public RequestBuilder cookie(String name, Cookie cookie) {
        headers.add(pair(HttpHeaders.COOKIE, toHttpHeader(name, cookie)));
        return this;
    }


    public RequestBuilder withQuery(String name, Object value) {
        return query(name, value);
    }

    @SuppressWarnings("unchecked")
    public RequestBuilder query(String name, Object value) {
        if (value != null) {
            QueryParameters parse = QueryParameters.parse(uri.query());
            uri = uri.query(UrlEncodedMessage.toString(parse.add(name, value.toString())));
        }
        return this;
    }

    public RequestBuilder withForm(String name, Object value) {
        return form(name, value);
    }

    public RequestBuilder withForms(FormParameters formParameters) {
        for (Pair<String, String> param : formParameters) {
            form(param.first(), param.second());
        }
        return this;
    }

    public RequestBuilder form(String name, Object value) {
        if (value == null) {
            return this;
        }

        if (sequence(headers).filter(by(first(String.class), is(equalIgnoringCase(HttpHeaders.CONTENT_TYPE)))).isEmpty()) {
            withHeader(HttpHeaders.CONTENT_TYPE, format("%s; charset=%s", MediaType.APPLICATION_FORM_URLENCODED, Entity.DEFAULT_CHARACTER_SET));
        }
        entity = FormParameters.parse(new String(entity)).
                add(name, value.toString()).
                toString().getBytes();
        return this;
    }

    public RequestBuilder withInput(byte[] input) {
        return input(input);
    }

    public RequestBuilder input(byte[] input) {
        this.entity = input;
        return this;
    }

    public Request call() throws Exception {
        return build();
    }

    public Request build() {
        return request(method, uri, headerParameters(headers), entity);
    }

    public static RequestBuilder get(Uri uri) {
        return new RequestBuilder(HttpMethod.GET, uri);
    }

    public static RequestBuilder get(String path) {
        return new RequestBuilder(HttpMethod.GET, path);
    }

    public static RequestBuilder post(Uri uri) {
        return new RequestBuilder(HttpMethod.POST, uri);
    }

    public static RequestBuilder post(String path) {
        return new RequestBuilder(HttpMethod.POST, path);
    }

    public static RequestBuilder put(Uri uri) {
        return new RequestBuilder(HttpMethod.PUT, uri);
    }

    public static RequestBuilder put(String path) {
        return new RequestBuilder(HttpMethod.PUT, path);
    }

    public static RequestBuilder delete(Uri uri) {
        return new RequestBuilder(HttpMethod.DELETE, uri);
    }

    public static RequestBuilder delete(String path) {
        return new RequestBuilder(HttpMethod.DELETE, path);
    }

    public Uri uri() {
        return uri;
    }

    public RequestBuilder uri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public RequestBuilder replaceHeader(String name, Object value) {
        removeHeaders(name);
        headers.add(pair(name, value.toString()));
        return this;
    }

    public RequestBuilder removeHeaders(String name) {
        removeHeaders(headers, name);
        return this;
    }

    public static void removeHeaders(List<Pair<String, String>> headers, String name) {
        for (Pair<String, String> header : sequence(headers).filter(where(first(String.class), equalIgnoringCase(name))).realise()) {
            headers.remove(header);
        }
    }
}