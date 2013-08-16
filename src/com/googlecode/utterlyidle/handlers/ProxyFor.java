package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;

import java.net.Proxy;

public interface ProxyFor {
    Option<Proxy> proxyFor(Uri uri);
}
