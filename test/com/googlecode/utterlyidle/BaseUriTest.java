package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.BaseUri.baseUri;
import static com.googlecode.utterlyidle.HttpHeaders.HOST;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_HOST;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static com.googlecode.utterlyidle.Request.get;

public class BaseUriTest {
    @Test
    public void whenNoHostDefaultToBasePath() throws Exception {
        assertThat(baseUri(get("foo"), basePath("/root/")), is(baseUri("/root/")));
    }

    @Test
    public void whenHostIsSetUseThat() throws Exception {
        assertThat(baseUri(get("foo").header(HOST, "server"), basePath("/root/")), is(baseUri("http://server/root/")));
    }

    @Test
    public void whenXForwardedHostIsSetUseThatOverHost() throws Exception {
        assertThat(baseUri(get("foo").header(HOST, "internal").header(X_FORWARDED_HOST, "external"), basePath("/root/")), is(baseUri("http://external/root/")));
    }

    @Test
    public void whenXForwardedProtoIsSetUseThatOverDefaultHttp() throws Exception {
        assertThat(baseUri(get("foo").header(HOST, "server").header(X_FORWARDED_PROTO, "https"), basePath("/root/")), is(baseUri("https://server/root/")));
    }
}
