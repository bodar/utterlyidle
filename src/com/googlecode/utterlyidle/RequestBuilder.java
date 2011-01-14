package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.io.Url;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import static com.googlecode.utterlyidle.Request.request;
import static com.googlecode.utterlyidle.io.Url.url;

public class RequestBuilder {
    private final List<Pair<String,String>> headers = new ArrayList<Pair<String, String>>();
    private final List<Pair<String,String>> query = new ArrayList<Pair<String, String>>();
    private final List<Pair<String,String>> form = new ArrayList<Pair<String, String>>();
    private InputStream input;
    private final String method;
    private final String path;

    public RequestBuilder(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public RequestBuilder accepting(String value) {
        return withHeader(HttpHeaders.ACCEPT, value);
    }

    public RequestBuilder withHeader(String name, String value) {
        headers.add(pair(name, value));
        return this;
    }

    public RequestBuilder withQuery(String name, String value) {
        query.add(pair(name, value));
        return this;
    }

    public RequestBuilder withForm(String name, String value) {
        if(sequence(headers).filter(by(first(String.class), is(equalIgnoringCase(HttpHeaders.CONTENT_TYPE)))).isEmpty()){
            withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        }
        form.add(pair(name, value));
        return this;
    }

    public RequestBuilder withInput(InputStream input) {
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

    public static InputStream input(FormParameters form, InputStream input) {
        if(form.size() > 0){
            return new ByteArrayInputStream(form.toString().getBytes());
        }
        if(input == null){
            new ByteArrayInputStream(new byte[0]);
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

}