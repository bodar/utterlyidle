package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.collections.PersistentSet;
import com.googlecode.totallylazy.collections.PersistentSortedSet;

import java.net.Proxy;

import static com.googlecode.totallylazy.Option.none;

public class HttpProxy implements ProxyFor {
    private final PersistentSet<String> allowed = PersistentSortedSet.constructors.sortedSet("http", "https");
    private final ProxyFor proxyFor;

    private HttpProxy(final ProxyFor proxyFor) {
        this.proxyFor = proxyFor;
    }

    public static HttpProxy httpProxy(final ProxyFor proxyFor) {
        return new HttpProxy(proxyFor);
    }

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        if (allowed.contains(uri.scheme().toLowerCase())) return proxyFor.proxyFor(uri);
        return none();
    }

    public static Mapper<Proxy, Proxy.Type> type = new Mapper<Proxy, Proxy.Type>() {
        @Override
        public Proxy.Type call(final Proxy proxy) throws Exception {
            return proxy.type();
        }
    };
}
