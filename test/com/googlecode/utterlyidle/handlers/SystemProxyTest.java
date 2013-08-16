package com.googlecode.utterlyidle.handlers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.handlers.SystemProxy.systemProxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SystemProxyTest {
    @BeforeClass
    public static void setProperties() {
        System.setProperty("http.proxyHost", "proxy");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
    }

    @AfterClass
    public static void clearProperties() {
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("http.nonProxyHosts");
    }

    @Test
    public void supportsHttp() throws Exception {
        Proxy proxy = systemProxy().proxyFor(uri("http://server")).get();

        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        assertThat(address.getHostName(), is("proxy"));
        assertThat(address.getPort(), is(8080));
    }

    @Test
    public void supportsNoProxyForLocalHost() throws Exception {
        assertThat(systemProxy().proxyFor(uri("http://localhost")), is(none(Proxy.class)));
    }

    @Test
    public void ignoresFileUrls() throws Exception {
        assertThat(systemProxy().proxyFor(uri("file:///home/dan")), is(none(Proxy.class)));
    }
}
