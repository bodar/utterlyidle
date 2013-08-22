package com.googlecode.utterlyidle.proxies;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;

import java.net.Proxy;

import static com.googlecode.totallylazy.Option.none;

public class NoProxy implements ProxyFor {
    private NoProxy() {
    }

    public static NoProxy noProxy() {
        return new NoProxy();
    }

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        return none();
    }
}
