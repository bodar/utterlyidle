package com.googlecode.utterlyidle;

import org.junit.Test;

import java.util.Properties;

import static com.googlecode.utterlyidle.ServerConfiguration.*;
import static java.lang.Integer.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class ServerConfigurationTest {

    @Test
    public void shouldLoadFromProperties() {
        String basePath = "//the//base//path//";
        String bindAddress = "1.2.3.4";
        String maxThreadNum = "355";
        String portNumber = "937";

        Properties properties = new Properties();
        properties.put(BASE_PATH, basePath);
        properties.put(BIND_ADDRESS, bindAddress);
        properties.put(MAX_THREAD_NUM, maxThreadNum);
        properties.put(PORT_NUMBER, portNumber);

        ServerConfiguration configuration = serverConfiguration(properties);

        assertThat(configuration.basePath().toString(), is(equalTo(basePath.replaceAll("//", "/"))));
        assertThat(configuration.bindAddress().getHostAddress(), is(equalTo(bindAddress)));
        assertThat(configuration.portNumber(), is(equalTo(valueOf(portNumber))));
        assertThat(configuration.maxThreadNumber(), is(equalTo(valueOf(maxThreadNum))));
    }
}
