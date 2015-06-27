package com.googlecode.utterlyidle.proxies;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.io.Uri;

import java.net.Proxy;

public interface ProxyFor {
    Option<Proxy> proxyFor(Uri uri);
}
