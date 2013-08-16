package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Map;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Uri.uri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ProxyForTest {
    @Test
    public void supportsSystemProperties() throws Exception {
        System.setProperty("http.proxyHost", "proxy");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");

        Proxy proxy = new DefaultProxySelector().proxyFor(uri("http://server")).get();

        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        assertThat(address.getHostName(), is("proxy"));
        assertThat(address.getPort(), is(8080));

        assertThat(new DefaultProxySelector().proxyFor(uri("http://localhost")), is(none(Proxy.class)));
        assertThat(new DefaultProxySelector().proxyFor(uri("file:///home/dan")), is(none(Proxy.class)));
    }

    @Test
    public void supportsEnvironmentVariable() throws Exception {
        Map<String, String> env = Maps.map(pair("http_proxy", "http://proxy:8080/"));
        Proxy proxy = EnvironmentProxySelector.environmentProxySelector(env).proxyFor(uri("http://server")).get();

        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        assertThat(address.getHostName(), is("proxy"));
        assertThat(address.getPort(), is(8080));
    }

    @Test
    public void supportsNoProxyEnvironmentVariable() throws Exception {
        Map<String, String> env = Maps.map(pair("http_proxy", "http://proxy:8080/"), pair("no_proxy", "localhost,127.0.0.0/8"));
        Option<Proxy> proxy = EnvironmentProxySelector.environmentProxySelector(env).proxyFor(uri("http://localhost"));

        assertThat(proxy, is(none(Proxy.class)));
    }

    @Test
    public void whenNoEnvironmentVariableGoDirect() throws Exception {
        Map<String, String> env = Maps.map();
        Option<Proxy> proxy = EnvironmentProxySelector.environmentProxySelector(env).proxyFor(uri("http://server"));

        assertThat(proxy, is(none(Proxy.class)));
    }
}
