package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class ServerConfigurationTest {

    @Test
    public void shouldLoadFromProperties() {
        int oldPort = 12943;
        int newPort = 9999;
        String path = "/soemthing/nothing/what/a/b;c=123?d=789";
        ServerConfiguration serverConfig = defaultConfiguration().port(oldPort).basePath(basePath(path));
        String url = serverConfig.toUrl().toString();
        assertThat(serverConfig.port(newPort).toUrl().toString(), is(equalTo(url.replaceFirst(valueOf(oldPort), valueOf(newPort)))));
    }
}
