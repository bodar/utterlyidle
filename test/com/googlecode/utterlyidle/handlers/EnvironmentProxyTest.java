package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Maps;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.handlers.EnvironmentProxy.environmentProxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EnvironmentProxyTest {
    @Test
    public void supportsEnvironmentVariable() throws Exception {
        Map<String, String> env = Maps.map(pair("http_proxy", "http://proxy:8080/"));
        Proxy proxy = environmentProxy(env).proxyFor(uri("http://server")).get();

        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        assertThat(address.getHostName(), is("proxy"));
        assertThat(address.getPort(), is(8080));
    }

    @Test
    public void supportsNoProxyEnvironmentVariable() throws Exception {
        Map<String, String> env = Maps.map(pair("http_proxy", "http://proxy:8080/"), pair("no_proxy", "localhost,127.0.0.0/8"));
        assertThat(environmentProxy(env).proxyFor(uri("http://localhost")), is(none(Proxy.class)));
    }

    @Test
    public void whenNoEnvironmentVariableGoDirect() throws Exception {
        assertThat(environmentProxy(Maps.<String, String>map()).proxyFor(uri("http://server")), is(none(Proxy.class)));
    }
}
