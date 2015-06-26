package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public class CookieParameters extends Parameters<String, String, CookieParameters> {
    private CookieParameters(PersistentList<Pair<String, String>> values) {
        super(equalIgnoringCase(), values);
    }

    @Override
    protected CookieParameters self(PersistentList<Pair<String, String>> values) {
        return new CookieParameters(values);
    }

    public static CookieParameters cookies() {
        return new CookieParameters(PersistentList.constructors.<Pair<String,String>>empty());
    }

    public static CookieParameters cookies(Request request) {
        return pairs(CookieCutter.cookies(request));
    }

    public static CookieParameters cookies(Response response) {
        return pairs(CookieCutter.cookies(response));
    }

    private static CookieParameters pairs(final Iterable<Cookie> cookies) {
        return new CookieParameters(sequence(cookies).map(asPair()).toPersistentList());
    }

    private static Function1<Cookie, Pair<String, String>> asPair() {
        return cookie -> cookie.toPair();
    }
}
