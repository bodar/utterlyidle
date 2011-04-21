package com.googlecode.utterlyidle.dsl;

import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.TestApplication;
import org.junit.Test;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.dsl.ActivatorBuilder.get;
import static com.googlecode.utterlyidle.dsl.ActivatorBuilder.queryParam;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DslTest {
    @Test
    public void supportsGet() throws Exception {
        TestApplication application = new TestApplication();
        application.add(get("/bar").resource(method(on(Bar.class).hello())));
        assertThat(application.responseAsString(RequestBuilder.get("/bar")), is("Hello"));
    }

    @Test
    public void supportsGetWithParameters() throws Exception {
        TestApplication application = new TestApplication();
        application.add(get("/bar").resource(method(on(Foo.class).say(queryParam(String.class, "value")))));
        assertThat(application.responseAsString(RequestBuilder.get("/bar").withQuery("value", "Dan")), is("Hello Dan"));
    }

    @Test
    public void supportsGetWithMultipleParameters() throws Exception {
        TestApplication application = new TestApplication();
        application.add(get("/bar").resource(method(on(Bob.class).say(queryParam(String.class, "firstName"), queryParam(String.class, "lastName")))));
        assertThat(application.responseAsString(RequestBuilder.get("/bar").withQuery("firstName", "Dan").withQuery("lastName", "Bodart")), is("Hello Dan Bodart"));
    }


    public static class Bar {
        public String hello(){
            return "Hello";
        }
    }

    public static class Foo {
        public String say(String value){
            return "Hello " + value;
        }
    }

    public static class Bob {
        public String say(String first, String last){
            return "Hello " + first + " " + last;
        }
    }

}