package com.googlecode.utterlyidle.handlers;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Properties;

public class Proxys {


    public static Proxy properties(final Properties properties) {
        String host = properties.getProperty("http.proxyHost");
        Integer port = Integer.valueOf(properties.getProperty("http.proxyPort"));
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }
}
