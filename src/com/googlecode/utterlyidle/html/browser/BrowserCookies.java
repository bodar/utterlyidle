package com.googlecode.utterlyidle.html.browser;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Maps.pairs;

public interface BrowserCookies<SELF extends BrowserCookies> {
    SELF clearCookies();

    SELF clearCookie(String name);

    String getCookie(String name);

    SELF setCookie(String name, String value);

    Sequence<Pair<String,String>> cookies();

    class constructors {
        public static BrowserCookies<BrowserCookies> browserCookies() {
            return new BrowserCookies<BrowserCookies>() {
                private final Map<String, String> cookies = new HashMap<String, String>();

                @Override
                public BrowserCookies clearCookies() {
                    cookies.clear();
                    return this;
                }

                @Override
                public BrowserCookies clearCookie(String name) {
                    cookies.remove(name);
                    return this;
                }

                @Override
                public String getCookie(String name) {
                    return cookies.get(name);
                }

                @Override
                public BrowserCookies setCookie(String name, String value) {
                    cookies.put(name, value);
                    return this;
                }

                @Override
                public Sequence<Pair<String,String>> cookies() {
                    return pairs(cookies);
                }
            };
        }
    }
}
