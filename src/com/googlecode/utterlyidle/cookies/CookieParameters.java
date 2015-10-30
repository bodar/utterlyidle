package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

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
        return cookies(sequence(cookies).map(Cookie::toPair));
    }

    public static CookieParameters cookies(Iterable<? extends Pair<String, String>> pairs) {
        if(pairs instanceof CookieParameters) return (CookieParameters) pairs;
        return new CookieParameters(sequence(pairs).toPersistentList());
    }

    public List<Cookie> toList() {
        return values.map(pair -> new Cookie(pair.first(), pair.second()));
    }
}
