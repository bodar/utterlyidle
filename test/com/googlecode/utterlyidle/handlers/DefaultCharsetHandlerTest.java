package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultCharsetHandlerTest {

    @Test
    public void passesThroughCharsetWhenSpecified() throws Exception {
        String contentType = "text/html; charset=\"UTF-16\"";
        ApplicationBuilder application = application().add(get("/bar").resource(method(on(Bar.class).hello())).produces(contentType));
        Response response = application.handle(RequestBuilder.get("/bar"));

        assertThat(response.header("Content-Type"), CoreMatchers.is(contentType));
    }

    @Test
    public void choosesUtf8WhenCharsetNotSpecified() throws Exception {
        ApplicationBuilder application = application().add(get("/bar").resource(method(on(Bar.class).hello())).produces("text/html"));
        Response response = application.handle(RequestBuilder.get("/bar"));

        assertThat(response.header("Content-Type"), CoreMatchers.is("text/html; charset=\"UTF-8\""));
    }

    public static class Bar {
        public String hello() {
            return "Hello";
        }
    }
}
