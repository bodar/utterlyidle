package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.ServerUrl.serverUrl;
import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class ServerConfigurationTest {

    @Test
    public void shouldLoadFromProperties() {
        int oldPort = 12943;
        int newPort = 9999;
        String url = "http://coolhost:" + oldPort + "/soemthing/nothing/what/a/b;c=123?d=789";
        ServerConfiguration serverConfig = ServerConfiguration.defaultConfiguration().serverUrl(serverUrl(url));
        assertThat(serverConfig.port(newPort).serverUrl().toString(), is(equalTo(url.replaceFirst(valueOf(oldPort), valueOf(newPort)))));
    }
}
