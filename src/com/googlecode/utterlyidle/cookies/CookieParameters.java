package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.Rfc2616;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Strings.empty;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.collections.PersistentList.constructors.list;
import static com.googlecode.totallylazy.regex.Regex.regex;

public class CookieParameters extends Parameters<String, String, CookieParameters> {
    private CookieParameters(PersistentList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected CookieParameters self(PersistentList<Pair<String, String>> values) {
        return new CookieParameters(values);
    }

    public static CookieParameters cookies(final HeaderParameters headerParameters) {
        return new CookieParameters(list(headerParameters.
                getValues(HttpHeaders.COOKIE).
                filter(not(empty())).
                flatMap(parseIntoPairs())));
    }

    public static Sequence<Pair<String, String>> cookies(String header) {
        return regex("\\s*;\\s*").
                split(header).
                map(splitOnFirst("=")).
                filter(not(anAttribute())).
                map(Callables.<String, String, String>second(Rfc2616.toUnquotedString()));
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

    public static String toHttpHeader(Cookie cookie) {
        return cookie.toString();
    }

    @Deprecated // delete after build 637
    public static String toHttpHeader(String name, Cookie cookie) {
        return toHttpHeader(cookie);
    }

    private static Function1<String, Sequence<Pair<String, String>>> parseIntoPairs() {
        return new Function1<String, Sequence<Pair<String, String>>>() {
            @Override
            public Sequence<Pair<String, String>> call(String header) throws Exception {
                return cookies(header);
            }
        };
    }
}