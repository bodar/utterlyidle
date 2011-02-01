package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Rfc2616;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;

public class CookieParameters extends Parameters<String, String> {
    private final Map<String, Cookie> newCookies = new HashMap<String, Cookie>();
    public static final String REQUEST_COOKIE_HEADER = "Cookie";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";

    private CookieParameters(Request request) {
        super(equalIgnoringCase());
        parseRequestCookies(sequence(request.headers().getValues(REQUEST_COOKIE_HEADER)));
    }

    @Override
    public Parameters add(String name, String value) {
        return super.add(name, Rfc2616.toUnquotedString(value));
    }

    public static CookieParameters cookies(Request request) {
        return new CookieParameters(request);
    }

    public CookieParameters set(String name, String value) {
        return set(name, cookie(value));
    }

    public CookieParameters set(String name, Cookie value) {
        newCookies.put(name, value);
        return this;
    }

    public String getValue(String name) {
        if (contains(name))
            return super.getValue(name);
        if (newCookies.containsKey(name))
            return newCookies.get(name).value();
        return null;
    }

    public Response commit(Response response) {
        return sequence(newCookies.entrySet()).fold(response, setCookiesAsHeaders());
    }

    public void rollback() {
        newCookies.clear();
    }

    private void parseRequestCookies(Sequence<String> headers) {
        for (String header : headers) {
            parseIntoPairs(header).fold(this, Parameters.<String, String>pairIntoParameters());
        }
    }

    private Sequence<Pair<String, String>> parseIntoPairs(String header) {
        return regex("\\s*;\\s*").
                split(header).
                map(splitOnFirst("=")).
                filter(not(anAttribute()));
    }

    private static Predicate<? super Pair<String, String>> anAttribute() {
        return new Predicate<Pair<String, String>>() {
            public boolean matches(Pair<String, String> pair) {
                return pair.first().startsWith("$");
            }
        };
    }

    private static Callable1<? super String, Pair<String, String>> splitOnFirst(final String separator) {
        return new Callable1<String, Pair<String, String>>() {
            public Pair<String, String> call(String cookie) throws Exception {
                return pair(cookie.substring(0, cookie.indexOf(separator)), cookie.substring(cookie.indexOf(separator) + 1, cookie.length()));
            }
        };
    }

    private Callable2<Response, Map.Entry<String,Cookie>, Response> setCookiesAsHeaders() {
        return new Callable2<Response, Map.Entry<String,Cookie>, Response>() {
            public Response call(Response response, Map.Entry<String,Cookie> entry) throws Exception {
                return response.header(SET_COOKIE_HEADER, toHttpHeader(entry.getKey(), entry.getValue()));
            }
        };
    }

    public static String toHttpHeader(String name, Cookie cookie) {
        final String cookieValue = String.format("%s=%s; ", name, Rfc2616.toQuotedString(cookie.value()));
        final String attributes = sequence(cookie.attributes()).toString("; ");

        return cookieValue + attributes;
    }

}
