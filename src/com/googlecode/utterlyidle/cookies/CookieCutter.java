package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.functions.Function1;
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
        return request.headers().getValues(COOKIE).flatMap(header -> parseRequestHeader(header));
    }

    public static Iterable<Cookie> cookies(Response response) {
        return flatten(response.headers().getValues(SET_COOKIE).map(header -> parseResponseHeader(header)));
    }

    public static Sequence<Cookie> parseRequestHeader(String value) {
        return parseNameValuePairs(value).map(buildCookie());
    }

    public static Option<Cookie> parseResponseHeader(String value) {
        return buildCookieWithAttributes(parseNameValuePairs(value));
    }

    private static Function1<Pair<String, String>, Cookie> buildCookie() {
        return header -> cookie(header.first(), header.second());
    }

    private static Option<Cookie> buildCookieWithAttributes(Sequence<Pair<String, String>> pairs) {
        if(pairs.isEmpty()) {
            return none();
        } else {
            return some(cookie(pairs.head().first(), pairs.head().second(), attributes(pairs.tail()).toArray(new CookieAttribute[pairs.tail().size()])));
        }
    }

    private static Sequence<CookieAttribute> attributes(Sequence<Pair<String, String>> attributes) {
        return attributes.map(pair -> cookieAttribute(pair.first(), pair.second()));
    }

    private static Sequence<Pair<String, String>> parseNameValuePairs(String header) {
        return regex("\\s*;\\s*").split(header)
                .filter(correctlyFormed())
                .map(splitOnFirst("="))
                .map(Callables.<String, String, String>second(Rfc2616.toUnquotedString()));
    }

    private static Predicate<? super String> correctlyFormed() {
        return cookie -> cookie.contains("=");
    }

    private static Function1<? super String, Pair<String, String>> splitOnFirst(final String separator) {
        return cookie -> pair(cookie.substring(0, cookie.indexOf(separator)), cookie.substring(cookie.indexOf(separator) + 1, cookie.length()));
    }
}
