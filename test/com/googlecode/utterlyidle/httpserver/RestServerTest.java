package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Runnable1;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.io.Url;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestServerTest {
    @Test
    public void stillsWorks() throws Exception {
        RestServer.main(null);
        InputAsString output = new InputAsString();
        Pair<Integer, String> status = Url.url("http://localhost:8000/helloWorld?name=foo").get("*/*", output);

        assertThat(status.first(), is(200));
        assertThat(output.value(), is("Hello foo"));
    }

    private static class InputAsString implements Runnable1<InputStream> {
        private String value;

        public void run(InputStream inputStream) {
            value = Strings.toString(inputStream);
        }

        public String value(){
            return value;
        }
    }
}
