package com.googlecode.utterlyidle.proxies;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;

import java.net.Proxy;

import static com.googlecode.totallylazy.Option.none;

public enum NoProxy implements ProxyFor { instance;
    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        return none();
    }
}
