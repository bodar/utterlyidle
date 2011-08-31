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
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.Requests.request;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;

public class RequestBuilder implements Callable<Request> {
    private final List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
    private final List<Pair<String, String>> form = new ArrayList<Pair<String, String>>();
    private byte[] input;
    private final String method;
    private Uri uri;

    public RequestBuilder(String method, Uri uri) {
        this.method = method;
        this.uri = uri;
    }

    public RequestBuilder(String method, String uri) {
        this(method, Uri.uri(uri));
    }

    public RequestBuilder(Request request) {
        this(request.method(), request.uri());

        sequence(request.headers()).fold(this, new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> nameAndValue) throws Exception {
                requestBuilder.withHeader(nameAndValue.first(), nameAndValue.second());
                return requestBuilder;
            }
        });
        sequence(Requests.form(request)).fold(this, new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> nameAndValue) throws Exception {
                requestBuilder.withForm(nameAndValue.first(), nameAndValue.second());
                return requestBuilder;
            }
        });
    }

    public RequestBuilder withUri(Uri value) {
        uri = value;
        return this;
    }
    
    public RequestBuilder accepting(String value) {
        return withHeader(HttpHeaders.ACCEPT, value);
    }

    public RequestBuilder withHeader(String name, String value) {
        headers.add(pair(name, value));
        return this;
    }

    public RequestBuilder withCookie(String name, Cookie cookie) {
        headers.add(pair(HttpHeaders.COOKIE, toHttpHeader(name, cookie)));
        return this;
    }


    public RequestBuilder withQuery(String name, String value) {
        uri = uri.query(UrlEncodedMessage.toString(QueryParameters.parse(uri.query()).add(name, value)));
        return this;
    }

    public RequestBuilder withForm(String name, String value) {
        if (sequence(headers).filter(by(first(String.class), is(equalIgnoringCase(HttpHeaders.CONTENT_TYPE)))).isEmpty()) {
            withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        }
        form.add(pair(name, value));
        return this;
    }

    public RequestBuilder withInput(byte[] input) {
        this.input = input;
        return this;
    }

    public Request call() throws Exception {
        return build();
    }

    public Request build() {
        return request(method, uri, headerParameters(headers), input(formParameters(form), input));
    }

    public static byte[] input(FormParameters form, byte[] input) {
        if (form.size() > 0) {
            if (input != null)
                throw new IllegalStateException("Please specify either form parameters or an input stream- not both.");
            return form.toString().getBytes();
        }
        if (input == null) {
            return new byte[0];
        }
        return input;
    }

    public static RequestBuilder get(String path) {
        return new RequestBuilder(HttpMethod.GET, path);
    }

    public static RequestBuilder post(String path) {
        return new RequestBuilder(HttpMethod.POST, path);
    }

    public static RequestBuilder put(String path) {
        return new RequestBuilder(HttpMethod.PUT, path);
    }

    public static RequestBuilder delete(String path) {
        return new RequestBuilder(HttpMethod.DELETE, path);
    }

    public Uri uri() {
        return uri;
    }
}