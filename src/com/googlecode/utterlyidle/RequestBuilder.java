package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.io.Url;

import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.List;

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
import static com.googlecode.utterlyidle.io.Url.url;

public class RequestBuilder {
    private final List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
    private final List<Pair<String, String>> query = new ArrayList<Pair<String, String>>();
    private final List<Pair<String, String>> form = new ArrayList<Pair<String, String>>();
    private byte[] input;
    private final String method;
    private String path;

    public RequestBuilder(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public RequestBuilder(Request request) {
        this(request.method(), request.url().toString());

        sequence(request.headers()).fold(this, new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> nameAndValue) throws Exception {
                requestBuilder.withHeader(nameAndValue.first(), nameAndValue.second());
                return requestBuilder;
            }
        });
        sequence(request.form()).fold(this, new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> nameAndValue) throws Exception {
                requestBuilder.withForm(nameAndValue.first(), nameAndValue.second());
                return requestBuilder;
            }
        });
    }

    public RequestBuilder withPath(String value) {
        path = value;
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
        query.add(pair(name, value));
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

    public Request build() {
        return request(method, buildUrl(), headerParameters(headers), input(formParameters(form), input));
    }

    private Url buildUrl() {
        String queryString = query.isEmpty() ? "" : "?" + UrlEncodedMessage.toString(query);
        return url(path + queryString);
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

    public String path() {
        return path;
    }
}