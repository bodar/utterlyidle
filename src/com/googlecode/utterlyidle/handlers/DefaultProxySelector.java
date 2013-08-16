package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.collections.PersistentSet;
import com.googlecode.totallylazy.collections.PersistentSortedSet;

import java.net.Proxy;
import java.net.ProxySelector;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.net.Proxy.Type.DIRECT;
import static java.net.ProxySelector.getDefault;

public class DefaultProxySelector implements ProxyFor {
    private final PersistentSet<String> allowed = PersistentSortedSet.constructors.sortedSet("http", "https");
    private final ProxySelector system = getDefault();

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        if (allowed.contains(uri.scheme().toLowerCase())) {
            return sequence(system.select(uri.toURI())).find(where(type, is(not(DIRECT))));
        }
        return none();
    }

    public static Mapper<Proxy, Proxy.Type> type = new Mapper<Proxy, Proxy.Type>() {
        @Override
        public Proxy.Type call(final Proxy proxy) throws Exception {
            return proxy.type();
        }
    };
}
