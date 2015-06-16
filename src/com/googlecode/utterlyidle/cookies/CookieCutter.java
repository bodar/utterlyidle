package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Rfc2616;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.flatten;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.HttpHeaders.SET_COOKIE;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.cookieAttribute;

public class CookieCutter {

    public static Iterable<Cookie> cookies(Request request) {
        return request.headers().getValues(COOKIE).flatMap(new Function<String, Iterable<Cookie>>() {
            @Override
            public Iterable<Cookie> call(String header) throws Exception {
                return parseRequestHeader(header);
            }
        });
    }

    public static Iterable<Cookie> cookies(Response response) {
        return flatten(response.headers().getValues(SET_COOKIE).map(new Function<String, Option<Cookie>>() {
            @Override
            public Option<Cookie> call(String header) throws Exception {
                return parseResponseHeader(header);
            }
        }));
    }

    public static Sequence<Cookie> parseRequestHeader(String value) {
        return parseNameValuePairs(value).map(buildCookie());
    }

    public static Option<Cookie> parseResponseHeader(String value) {
        return buildCookieWithAttributes(parseNameValuePairs(value));
    }

    private static Function<Pair<String, String>, Cookie> buildCookie() {
        return new Function<Pair<String, String>, Cookie>() {
            @Override
            public Cookie call(final Pair<String, String> header) throws Exception {
                return cookie(header.first(), header.second());
            }
        };
    }

    private static Option<Cookie> buildCookieWithAttributes(Sequence<Pair<String, String>> pairs) {
        if(pairs.isEmpty()) {
            return none();
        } else {
            return some(cookie(pairs.head().first(), pairs.head().second(), attributes(pairs.tail()).toArray(new CookieAttribute[pairs.tail().size()])));
        }
    }

    private static Sequence<CookieAttribute> attributes(Sequence<Pair<String, String>> attributes) {
        return attributes.map(new Function<Pair<String, String>, CookieAttribute>() {
            @Override
            public CookieAttribute call(final Pair<String, String> pair) throws Exception {
                return cookieAttribute(pair.first(), pair.second());
            }
        });
    }

    private static Sequence<Pair<String, String>> parseNameValuePairs(String header) {
        return regex("\\s*;\\s*").split(header)
                .filter(correctlyFormed())
                .map(splitOnFirst("="))
                .map(Callables.<String, String, String>second(Rfc2616.toUnquotedString()));
    }

    private static Predicate<? super String> correctlyFormed() {
        return new Predicate<String>() {
            @Override
            public boolean matches(final String cookie) {
                return cookie.contains("=");
            }
        };
    }

    private static Function<? super String, Pair<String, String>> splitOnFirst(final String separator) {
        return new Function<String, Pair<String, String>>() {
            public Pair<String, String> call(String cookie) throws Exception {
                return pair(cookie.substring(0, cookie.indexOf(separator)), cookie.substring(cookie.indexOf(separator) + 1, cookie.length()));
            }
        };
    }
}
