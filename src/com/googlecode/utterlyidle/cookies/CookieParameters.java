package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.List;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.Unchecked.cast;

public class CookieParameters extends Parameters<CookieParameters> {
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

    public static CookieParameters pairs(final Iterable<? extends Cookie> cookies) {
        return cookies(sequence(cookies));
    }

    public static CookieParameters cookies(Iterable<? extends Pair<String, String>> pairs) {
        if(pairs instanceof CookieParameters) return (CookieParameters) pairs;
        if(pairs instanceof PersistentList) return new CookieParameters(cast(pairs));
        return new CookieParameters(sequence(pairs).toPersistentList());
    }

    public CookieParameters add(Cookie cookie) {
        return self(values.append(cookie));
    }

    public CookieParameters replace(Cookie cookie) {
        return remove(cookie.name()).add(cookie);
    }

    public Option<Cookie> get(String name){
        return find(p -> p.first().equals(name)).map(CookieParameters::toCookie);
    }

    public List<Cookie> toList() {
        return values.map(CookieParameters::toCookie);
    }

    public static Cookie toCookie(Pair<String, String> pair) {
        if (pair instanceof Cookie) return (Cookie) pair;
        return new Cookie(pair.first(), pair.second());
    }
}