package com.googlecode.utterlyidle.proxies;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Uri;

import java.net.Proxy;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.proxies.EnvironmentProxy.environmentProxy;

public class Proxies implements ProxyFor {
    private final Sequence<ProxyFor> proxies;

    private Proxies(final Iterable<? extends ProxyFor> proxies) {
        this.proxies = sequence(proxies);
    }

    public static ProxyFor proxies(final Iterable<? extends ProxyFor> proxies) {
        return HttpProxy.httpProxy(new Proxies(proxies));
    }

    public static ProxyFor autodetectProxies() { // Use system properties first, then environment variables
        return proxies(Sequences.sequence(ProxyAdapter.systemProxy(), EnvironmentProxy.environmentProxy()));
    }

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        return proxies.flatMap(new Mapper<ProxyFor, Option<Proxy>>() {
            @Override
            public Option<Proxy> call(final ProxyFor proxyFor) throws Exception {
                return proxyFor.proxyFor(uri);
            }
        }).headOption();
    }

    public Sequence<ProxyFor> proxies() {
        return proxies;
    }
}
