package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.Cookie.cookie;

public class Cookies {
    private final Map<String, Cookie> requestCookies;
    private final Map<String, Cookie> newCookies = new HashMap<String, Cookie>();
    private final Response response;

    public static Cookies cookies(Request request, Response response) {
        return new Cookies(request, response);
    }

    public Cookies(Request request, Response response) {
        this.response = response;
        this.requestCookies = parseRequestCookies(request.headers().getValue("Cookie"));
    }

    public Cookies set(String name, String value) {
        return set(cookie(name, value));
    }

    public Cookies set(Cookie value) {
        newCookies.put(value.getName(), value);
        return this;
    }

    public Cookie get(String name) {
        if (requestCookies.containsKey(name))
            return requestCookies.get(name);
        if (newCookies.containsKey(name))
            return newCookies.get(name);
        return null;
    }

    public String getValue(String name) {
        final Cookie cookie = get(name);
        return cookie == null ? null : cookie.getValue();
    }


    public void commit() {
        sequence(newCookies.values()).fold(response, setCookiesAsHeaders());
    }

    public void rollback() {
        newCookies.clear();
    }

    public static Map<String, Cookie> parseRequestCookies(String header) {
        Map<String, Cookie> cookies = new HashMap<String, Cookie>();
        if(header==null) return cookies;
        return regex(";").
                split(header).
                map(trimLeft()).
                map(splitOnFirst("=")).
                filter(not(anAttribute())).
                foldLeft(cookies, pairsIntoMap());
    }

    private static Predicate<? super Pair<String, String>> anAttribute() {
        return new Predicate<Pair<String, String>>() {
            public boolean matches(Pair<String, String> pair) {
                return pair.first().startsWith("$");
            }
        };
    }

    private static Callable1<? super String, String> trimLeft() {
        return new Callable1<String, String>() {
            public String call(String s) throws Exception {
                return s.replaceFirst("^\\s*", "");
            }
        };
    }

    public static Callable2<Map<String, Cookie>, Pair<String, String>, Map<String, Cookie>> pairsIntoMap() {
        return new Callable2<Map<String, Cookie>, Pair<String, String>, Map<String, Cookie>>() {
            public Map<String, Cookie> call(Map<String, Cookie> map, Pair<String, String> nameValue) throws Exception {
                map.put(nameValue.first(), cookie(nameValue.first(), nameValue.second()));
                return map;
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

    private Callable2<Response, Cookie, Response> setCookiesAsHeaders() {
        return new Callable2<Response, Cookie, Response>() {
            public Response call(Response response, Cookie cookie) throws Exception {
                response.headers().add("Set-Cookie", cookie.toHttpHeader());
                return response;
            }
        };
    }
}
