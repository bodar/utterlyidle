package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.Request.Builder;
import org.junit.Test;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.Lists.list;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.Parameters.Builder.add;
import static com.googlecode.utterlyidle.Request.Builder.cookie;
import static com.googlecode.utterlyidle.Request.delete;
import static com.googlecode.utterlyidle.HttpMessage.Builder.entity;
import static com.googlecode.utterlyidle.Request.Builder.form;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.Request.head;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.Request.Builder.method;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.Request.options;
import static com.googlecode.utterlyidle.Parameters.Builder.param;
import static com.googlecode.utterlyidle.Request.patch;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.Request.put;
import static com.googlecode.utterlyidle.Request.Builder.query;
import static com.googlecode.utterlyidle.Parameters.Builder.remove;
import static com.googlecode.utterlyidle.Request.request;
import static com.googlecode.utterlyidle.annotations.HttpMethod.DELETE;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.annotations.HttpMethod.HEAD;
import static com.googlecode.utterlyidle.annotations.HttpMethod.OPTIONS;
import static com.googlecode.utterlyidle.annotations.HttpMethod.PATCH;
import static com.googlecode.utterlyidle.annotations.HttpMethod.POST;
import static com.googlecode.utterlyidle.annotations.HttpMethod.PUT;

public class RequestTest {
    @Test
    public void supportsGet() throws Exception {
        Request request = Request.get("http://localhost/");
        assertThat(request.method(), is(GET));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsPost() throws Exception {
        Request request = Request.post("http://localhost/");
        assertThat(request.method(), is(POST));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsPut() throws Exception {
        Request request = Request.put("http://localhost/");
        assertThat(request.method(), is(PUT));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsPatch() throws Exception {
        Request request = Request.patch("http://localhost/");
        assertThat(request.method(), is(PATCH));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsDelete() throws Exception {
        Request request = Request.delete("http://localhost/");
        assertThat(request.method(), is(DELETE));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsHead() throws Exception {
        Request request = Request.head("http://localhost/");
        assertThat(request.method(), is(HEAD));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsOptions() throws Exception {
        Request request = Request.options("http://localhost/");
        assertThat(request.method(), is(OPTIONS));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsCustomMethod() throws Exception {
        Request request = Request.request("TICKLE", Uri.uri("http://localhost/"));
        assertThat(request.method(), is("TICKLE"));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void canChangeMethod() throws Exception {
        Request request = modify(Request.get("/"), method(POST));
        assertThat(request.method(), is(POST));
    }

    @Test
    public void canChangeUri() throws Exception {
        Request request = modify(Request.get("/"), Builder.uri("/different"));
        assertThat(request.uri(), is(uri("/different")));
    }

    @Test
    public void canSetHeaderParameters() throws Exception {
        assertThat(Request.get("/", HttpMessage.Builder.header(ACCEPT, "Chickens")).headers().getValue(ACCEPT), is("Chickens"));
        HeaderParameters headers = Request.get("/", HttpMessage.Builder.header(ACCEPT, "Chickens"), HttpMessage.Builder.header(CONTENT_TYPE, "Cats")).headers();
        assertThat(headers.getValue(ACCEPT), is("Chickens"));
        assertThat(headers.getValue(CONTENT_TYPE), is("Cats"));
    }

    @Test
    public void canSetMultipleHeaderParametersInOneGoForPerformanceReasons() throws Exception {
        assertThat(Request.get("/", HttpMessage.Builder.header(Parameters.Builder.param(ACCEPT, list("Chickens", "Cats")))).headers().getValues(ACCEPT), is(sequence("Chickens", "Cats")));
        assertThat(Request.get("/", HttpMessage.Builder.header(add(ACCEPT, "Chickens"), add(ACCEPT, "Cats"))).headers().getValues(ACCEPT), is(sequence("Chickens", "Cats")));
    }

    @Test
    public void canRemoveAHeader() throws Exception {
        Request original = Request.get("/", HttpMessage.Builder.header(ACCEPT, "Chickens"), HttpMessage.Builder.header(CONTENT_TYPE, "Cats"));
        HeaderParameters headers = modify(original, HttpMessage.Builder.header(remove(ACCEPT))).headers();
        assertThat(headers.contains(ACCEPT), is(false));
        assertThat(headers.getValue(CONTENT_TYPE), is("Cats"));
    }

    @Test
    public void canSetEntity() throws Exception {
        Request request = Request.get("/", entity("Hello"));
        assertThat(request.entity().toString(), is("Hello"));
    }

    @Test
    public void canSetQueryParameters() throws Exception {
        assertThat(Request.get("/", query("name", "Dan")).uri(), is(uri("/?name=Dan")));
        assertThat(Request.get("/", query("first", "Dan"), query("last", "Bod")).uri(), is(uri("/?first=Dan&last=Bod")));
    }

    @Test
    public void canSetMultipleQueryParametersInOneGoForPerformanceReasons() throws Exception {
        assertThat(Request.get("/", query(Parameters.Builder.param("name", list("Dan", "Matt")))).uri(), is(uri("/?name=Dan&name=Matt")));
        assertThat(Request.get("/", query(add("name", "Dan"), add("name", "Matt"))).uri(), is(uri("/?name=Dan&name=Matt")));
    }

    @Test
    public void canRemoveAQuery() throws Exception {
        Request original = Request.get("/", query("first", "Dan"), query("last", "Bod"));
        assertThat(modify(original, query(remove("first"))).uri(), is(uri("/?last=Bod")));
    }

    @Test
    public void canSetFormParameters() throws Exception {
        assertThat(Request.get("/", form("name", "Dan")).entity().toString(), is("name=Dan"));
        assertThat(Request.get("/", form("first", "Dan"), form("last", "Bod")).entity().toString(), is("first=Dan&last=Bod"));
    }

    @Test
    public void canSetMultipleFormParametersInOneGoForPerformanceReasons() throws Exception {
        assertThat(Request.get("/", form(Parameters.Builder.param("name", list("Dan", "Matt")))).entity().toString(), is("name=Dan&name=Matt"));
        assertThat(Request.get("/", form(add("name", "Dan"), add("name", "Matt"))).entity().toString(), is("name=Dan&name=Matt"));
    }

    @Test
    public void canRemoveAForm() throws Exception {
        Request original = Request.get("/", form("first", "Dan"), form("last", "Bod"));
        assertThat(modify(original, form(remove("first"))).entity().toString(), is("last=Bod"));
    }

    @Test
    public void canSetCookieParameters() throws Exception {
        String value = Request.get("/", cookie("name", "Dan")).headers().getValue(COOKIE);
        assertThat(value, is("name=\"Dan\"; "));
        assertThat(Request.get("/", cookie("first", "Dan"), cookie("last", "Bod")).headers().getValues(COOKIE), is(sequence("first=\"Dan\"; ", "last=\"Bod\"; ")));
    }

    @Test
    public void canSetMultipleCookieParametersInOneGoForPerformanceReasons() throws Exception {
        assertThat(Request.get("/", cookie(Parameters.Builder.param("name", list("Dan", "Matt")))).headers().getValues(COOKIE), is(sequence("name=\"Dan\"; ", "name=\"Matt\"; ")));
        assertThat(Request.get("/", cookie(add("name", "Dan"), add("name", "Matt"))).headers().getValues(COOKIE), is(sequence("name=\"Dan\"; ", "name=\"Matt\"; ")));
    }

    @Test
    public void canRemoveACookie() throws Exception {
        Request original = Request.get("/", cookie("first", "Dan"), cookie("last", "Bod"));
        assertThat(modify(original, cookie(remove("first"))).headers().getValues(COOKIE), is(sequence("last=\"Bod\"; ")));
    }

}
