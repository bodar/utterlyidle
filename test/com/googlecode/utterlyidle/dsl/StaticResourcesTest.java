package com.googlecode.utterlyidle.dsl;

import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.googlecode.totallylazy.Files.workingDirectory;
import static com.googlecode.totallylazy.io.URLs.packageUrl;
import static com.googlecode.totallylazy.io.URLs.url;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static org.hamcrest.MatcherAssert.assertThat;

public class StaticResourcesTest {
    @Test
    public void handlesFilesInRoot() throws Exception {
        String response = application().add(bindings(in(packageUrl(StaticResourcesTest.class)).path(""))).responseAsString(get("test.js"));
        assertThat(response, is("{}"));
    }

    @Test
    public void doesNotAllowAccessToParentDirectories() throws Exception {
        URL build = url(new File(workingDirectory(), "build"));
        Response response = application().add(bindings(in(build).path(""))).handle(get("../build.xml"));
        assertThat(response.status().isClientError(), is(true));
    }
}