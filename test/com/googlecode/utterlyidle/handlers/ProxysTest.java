package com.googlecode.utterlyidle.handlers;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProxysTest {
    @Test
    public void supportsStandardProperties() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("http.proxyHost", "proxy");
        properties.setProperty("http.proxyPort", "8080");
//        properties.setProperty("http.nonProxyHosts", "localhost|127.0.0.1"); //TODO

        Proxy proxy = Proxys.properties(properties);
        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        assertThat(address.getHostName(), is("proxy"));
        assertThat(address.getPort(), is(8080));
    }
}
