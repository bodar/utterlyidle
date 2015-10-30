package com.googlecode.utterlyidle.dsl;

import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request.Builder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Responses;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import java.io.InputStream;

import static com.googlecode.totallylazy.Strings.string;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.Request.Builder.accept;
import static com.googlecode.utterlyidle.Request.Builder.query;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.annotations.View.constructors.view;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.definedParam;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.entity;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.patch;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.put;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.queryParam;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DslTest {
    @Test
    public void supportsPatch() throws Exception {
        ApplicationBuilder application = application().
                add(patch("baz").resource(method(on(Baz.class).input(entity()))));
        String response = application.responseAsString(Builder.patch("/baz", Builder.entity("Hello")));
        assertThat(response, is("Hello"));
    }

    @Test
    public void supportsEntityInputStream() throws Exception {
        ApplicationBuilder application = application().
                add(put("baz").resource(method(on(Baz.class).input(entity()))));
        String response = application.responseAsString(Builder.put("/baz", Builder.entity("Hello")));
        assertThat(response, is("Hello"));
    }

    @Test
    public void supportsDefaultingViewToMethodName() throws Exception {
        Binding binding = get("bar").resource(method(on(Bar.class).hello())).build();
        assertThat(binding.view(), is(view("hello")));
    }

    @Test
    public void supportsDefiningTheView() throws Exception {
        Binding binding = get("bar").view("foo").resource(method(on(Bar.class).hello())).build();
        assertThat(binding.view(), is(view("foo")));
    }

    @Test
    public void supportsRedirection() throws Exception {
        ApplicationBuilder application = application().
                add(get("redirect").resource(method(on(Redirect.class).redirect()))).
                add(get("target").resource(method(on(Redirect.class).target())));
        Response response = application.handle(Builder.get("/redirect"));
        assertThat(response.status(), is(Status.SEE_OTHER));
        assertThat(header(response, LOCATION), is("/target"));
    }

    @Test
    public void supportsGet() throws Exception {
        ApplicationBuilder application = application().add(get("/bar").resource(method(on(Bar.class).hello())));
        assertThat(application.responseAsString(Builder.get("/bar")), is("Hello"));
    }

    @Test
    public void supportsGetWithParameters() throws Exception {
        ApplicationBuilder application = application().add(get("/bar").resource(method(on(Foo.class).say(queryParam(String.class, "value")))));
        assertThat(application.responseAsString(Builder.get("/bar", query("value", "Dan"))), is("Hello Dan"));
    }

    @Test
    public void supportsGetWithDefaultValue() throws Exception {
        ApplicationBuilder application = application().add(get("/hello").resource(method(on(Foo.class).say(queryParam(String.class, "name", "Matt")))));
        assertThat(application.responseAsString(Builder.get("/hello")), is("Hello Matt"));
        assertThat(application.responseAsString(Builder.get("/hello", query("name", "Dan"))), is("Hello Dan"));
    }

    @Test
    public void supportsDefinedParameter() throws Exception {
        ApplicationBuilder application = application().add(get("/hello").resource(method(on(Foo.class).say(definedParam("Matt")))));
        assertThat(application.responseAsString(Builder.get("/hello")), is("Hello Matt"));
        assertThat(application.responseAsString(Builder.get("/hello", query("name", "Dan"))), is("Hello Matt"));
    }

    @Test
    public void supportsGetWithMultipleParameters() throws Exception {
        ApplicationBuilder application = application().add(get("/bar").resource(method(on(Bob.class).say(queryParam(String.class, "firstName"), queryParam(String.class, "lastName")))));
        assertThat(application.responseAsString(Builder.get("/bar", query("firstName", "Dan"), query("lastName", "Bodart"))), is("Hello Dan Bodart"));
    }

    @Test
    public void supportsProduces() throws Exception {
        ApplicationBuilder application = application().add(get("/bar").produces("text/html", "text/xml").resource(method(on(Bar.class).hello())));
        assertThat(application.responseAsString(Builder.get("/bar", accept("text/html"))), is("Hello"));
        assertThat(application.responseAsString(Builder.get("/bar", accept("text/xml"))), is("Hello"));
        assertThat(application.handle(Builder.get("/bar", accept("text/plain"))).status(), is(Status.NOT_ACCEPTABLE));
    }

    public static class Baz {
        public String input(InputStream inputStream){
            return string(inputStream);
        }
    }

    public static class Bar {
        public String hello() {
            return "Hello";
        }
    }

    public static class Foo {
        public String say(String value) {
            return "Hello " + value;
        }
    }

    public static class Bob {
        public String say(String first, String last) {
            return "Hello " + first + " " + last;
        }
    }

    public static class Redirect {
        private final Redirector redirector;

        public Redirect(Redirector redirector) {
            this.redirector = redirector;
        }

        public Response redirect() {
            return redirector.seeOther(method(on(Redirect.class).target()));
        }

        public Response redirect(String foo) {
            return redirector.seeOther(method(on(Redirect.class).target()));
        }

        public Response target() {
            return Responses.response(Status.NO_CONTENT);
        }
    }

}