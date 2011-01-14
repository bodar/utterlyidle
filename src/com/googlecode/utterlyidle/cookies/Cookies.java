package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Rfc2616;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieName.cookieName;

public class Cookies {
    private final Map<CookieName, Cookie> requestCookies;
    private final Map<CookieName, Cookie> newCookies = new HashMap<CookieName, Cookie>();
    private final Response response;
    public static final String REQUEST_COOKIE_HEADER = "Cookie";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";

    public static Cookies cookies(Request request, Response response) {
        return new Cookies(request, response);
    }

    public Cookies(Request request, Response response) {
        this.requestCookies = parseRequestCookies(sequence(request.headers().getValues(REQUEST_COOKIE_HEADER)));
        this.response = response;
    }

    public Cookies set(CookieName name, String value) {
        return set(cookie(name, value));
    }

    public Cookies set(Cookie value) {
        newCookies.put(value.getName(), value);
        return this;
    }

    public Cookie get(CookieName name) {
        if (requestCookies.containsKey(name))
            return requestCookies.get(name);
        if (newCookies.containsKey(name))
            return newCookies.get(name);
        return null;
    }

    public String getValue(CookieName name) {
        final Cookie cookie = get(name);
        return cookie == null ? null : cookie.getValue();
    }

    public void commit() {
        sequence(newCookies.values()).fold(response, setCookiesAsHeaders());
    }

    public void rollback() {
        newCookies.clear();
    }

    public static Map<CookieName, Cookie> parseRequestCookies(Sequence<String> headers) {
        return headers.fold(new HashMap<CookieName, Cookie>(), parseRequestCookie());
    }

    private static Callable2<? super HashMap<CookieName, Cookie>, ? super String, HashMap<CookieName, Cookie>> parseRequestCookie() {
        return new Callable2<HashMap<CookieName, Cookie>, String, HashMap<CookieName, Cookie>>() {
            public HashMap<CookieName, Cookie> call(HashMap<CookieName, Cookie> cookies, String header) throws Exception {
                regex("\\s*;\\s*").
                        split(header).
                        map(splitOnFirst("=")).
                        filter(not(anAttribute())).
                        foldLeft(cookies, pairToCookie());
                return cookies;
            }
        };
    }


    private static Predicate<? super Pair<String, String>> anAttribute() {
        return new Predicate<Pair<String, String>>() {
            public boolean matches(Pair<String, String> pair) {
                return pair.first().startsWith("$");
            }
        };
    }

    public static Callable2<Map<CookieName, Cookie>, Pair<String, String>, Map<CookieName, Cookie>> pairToCookie() {
        return new Callable2<Map<CookieName, Cookie>, Pair<String, String>, Map<CookieName, Cookie>>() {
            public Map<CookieName, Cookie> call(Map<CookieName, Cookie> map, Pair<String, String> nameValue) throws Exception {
                final CookieName name = cookieName(nameValue.first());
                map.put(name, cookie(name, Rfc2616.toUnquotedString(nameValue.second())));
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
                return response.header(SET_COOKIE_HEADER, cookie.toHttpHeader());
            }
        };
    }
}
