package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.io.Uri;
import org.junit.Test;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.utterlyidle.Request.Builder.*;
import static com.googlecode.utterlyidle.Request.Builder.request;
import static com.googlecode.utterlyidle.annotations.HttpMethod.*;

public class RequestTest {
    @Test
    public void supportsGet() throws Exception {
        Request request = get("http://localhost/");
        assertThat(request.method(), is(GET));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsPost() throws Exception {
        Request request = post("http://localhost/");
        assertThat(request.method(), is(POST));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsPut() throws Exception {
        Request request = put("http://localhost/");
        assertThat(request.method(), is(PUT));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsPatch() throws Exception {
        Request request = patch("http://localhost/");
        assertThat(request.method(), is(PATCH));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsDelete() throws Exception {
        Request request = delete("http://localhost/");
        assertThat(request.method(), is(DELETE));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsHead() throws Exception {
        Request request = head("http://localhost/");
        assertThat(request.method(), is(HEAD));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsOptions() throws Exception {
        Request request = options("http://localhost/");
        assertThat(request.method(), is(OPTIONS));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }

    @Test
    public void supportsCustomMethod() throws Exception {
        Request request = request("TICKLE", Uri.uri("http://localhost/"));
        assertThat(request.method(), is("TICKLE"));
        assertThat(request.uri(), is(uri("http://localhost/")));
    }
}
