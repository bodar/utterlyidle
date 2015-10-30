package com.googlecode.utterlyidle.proxies;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.match;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.regex.Regex;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

import static com.googlecode.totallylazy.Maps.find;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Strings.endsWith;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.io.Uri.functions.uri;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.matches;
import static com.googlecode.totallylazy.regex.Regex.regex;

public class EnvironmentProxy implements ProxyFor {
    private final Option<Proxy> http_proxy;
    private final Sequence<Predicate<String>> noProxy;

    private EnvironmentProxy(final Map<String, String> env) {
        http_proxy = httpProxy(env).flatMap(uri.optional()).map(this::createProxy);
        noProxy = find(env, equalIgnoringCase("no_proxy")).toSequence().
                flatMap(regex(",")::split).
                map(toPredicate).
                cons(is("127.0.0.1")).
                cons(is("localhost")).
                realise();
    }

    public static Option<String> httpProxy(final Map<String, String> env) {
        return find(env, equalIgnoringCase("http_proxy"));
    }

    private final Regex wildcard = Regex.regex("\\*(.+)");

    private final Function1<String, Predicate<String>> toPredicate = value -> new match<String, Predicate<String>>(wildcard) {
        Predicate<String> value(String suffix) {
            return endsWith(suffix);
        }
    }.apply(value).getOrElse(is(value));

    public static ProxyFor environmentProxy(final Map<String, String> env) {
        return HttpProxy.httpProxy(new EnvironmentProxy(env));
    }

    public static ProxyFor environmentProxy() {
        return environmentProxy(System.getenv());
    }

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        if (noProxy.exists(matches(uri.host()))) return none();
        return http_proxy;
    }

    private Proxy createProxy(final Uri uri) {
        return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(uri.host(), port(uri.authority())));
    }

    private final Regex r = regex(".*:(\\d+)");

    private int port(final String authority) {
        return Integer.valueOf(some(r.match(authority).group(1)).getOrElse("80"));
    }

}