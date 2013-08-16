package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.regex.Regex;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.googlecode.totallylazy.Lists.list;
import static com.googlecode.totallylazy.Maps.find;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.Uri.functions.uri;
import static com.googlecode.totallylazy.regex.Regex.regex;

public class EnvironmentProxySelector implements ProxyFor {
    private final Option<Proxy> http_proxy;
    private final Set<String> noProxy;

    private EnvironmentProxySelector(final Map<String, String> env) {
        http_proxy = find(env, equalIgnoringCase("http_proxy")).map(uri).map(proxyFor);
        noProxy = find(env, equalIgnoringCase("no_proxy")).toSequence().flatMap(split(regex(","))).toSet();
    }

    public static EnvironmentProxySelector environmentProxySelector(final Map<String, String> env) {
        return new EnvironmentProxySelector(env);
    }

    public static EnvironmentProxySelector environmentProxySelector() {
        return new EnvironmentProxySelector(System.getenv());
    }

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        if (noProxy.contains(uri.host())) return none();
        return http_proxy;
    }

    private Proxy createProxy(final Uri uri) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.host(), port(uri.authority())));
    }

    private final Mapper<Uri, Proxy> proxyFor = new Mapper<Uri, Proxy>() {
        @Override
        public Proxy call(final Uri value) throws Exception {
            return createProxy(value);
        }
    };

    private final Regex r = regex(".*:(\\d+)");

    private int port(final String authority) {
        return Integer.valueOf(some(r.match(authority).group(1)).getOrElse("80"));
    }

    private static Mapper<String, Sequence<String>> split(final Regex r) {
        return new Mapper<String, Sequence<String>>() {
            @Override
            public Sequence<String> call(final String s) throws Exception {
                return r.split(s);
            }
        };
    }
}