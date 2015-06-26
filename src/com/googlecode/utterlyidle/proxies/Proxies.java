package com.googlecode.utterlyidle.proxies;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
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

    public static ProxyFor autodetectProxies() {
        if(System.getProperty("http.proxyHost") != null) return ProxyAdapter.systemProxy();
        if(EnvironmentProxy.httpProxy(System.getenv()).isDefined()) return EnvironmentProxy.environmentProxy();
        return NoProxy.instance;
    }

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        return proxies.flatMap(proxyFor -> proxyFor.proxyFor(uri)).headOption();
    }

    public Sequence<ProxyFor> proxies() {
        return proxies;
    }
}
