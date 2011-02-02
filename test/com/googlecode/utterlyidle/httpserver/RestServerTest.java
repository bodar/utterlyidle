package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Runnable1;
import com.googlecode.utterlyidle.io.Url;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestServerTest {
    @Test
    public void stillsWorks() throws Exception {
        RestServer.main(null);
        Pair<Integer, String> status = Url.url("http://localhost:8000/helloWorld?name=foo").get("*/*", new Runnable1<InputStream>() {
            public void run(InputStream inputStream) {
                // ignore
            }
        });

        assertThat(status.first(), is(200));
    }
}
