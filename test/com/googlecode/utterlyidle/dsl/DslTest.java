package com.googlecode.utterlyidle.dsl;

import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.TestApplication;
import org.junit.Test;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.dsl.ActivatorBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DslTest {
    @Test
    public void supportsGet() throws Exception {
        TestApplication application = new TestApplication();
        application.add(get("/bar").resource(method(on(Bar.class).hello())));
        assertThat(application.responseAsString(RequestBuilder.get("/bar")), is("Hello"));
    }

    public static class Bar {
        public String hello(){
            return "Hello";
        }
    }

}