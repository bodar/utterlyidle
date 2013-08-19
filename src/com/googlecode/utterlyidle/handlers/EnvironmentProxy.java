package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.regex.Regex;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.Set;

import static com.googlecode.totallylazy.Lists.list;
import static com.googlecode.totallylazy.Maps.find;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.Uri.functions.uri;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.handlers.HttpProxy.httpProxy;

public class EnvironmentProxy implements ProxyFor {
    private final Option<Proxy> http_proxy;
    private final Set<String> noProxy;

    private EnvironmentProxy(final Map<String, String> env) {
        http_proxy = find(env, equalIgnoringCase("http_proxy")).map(uri).map(proxyFor);
        noProxy = find(env, equalIgnoringCase("no_proxy")).toSequence().flatMap(split(regex(","))).
                add("127.0.0.1").
                add("localhost").toSet();
    }

    public static ProxyFor environmentProxy(final Map<String, String> env) {
        return httpProxy(new EnvironmentProxy(env));
    }

    public static ProxyFor environmentProxy() {
        return environmentProxy(System.getenv());
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