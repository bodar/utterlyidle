package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RecordingHttpHandler;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.authentication.Credentials.credential;
import static com.googlecode.utterlyidle.handlers.RecordingHttpHandler.recordingHttpHandler;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicHttpClientTest {
    @Test
    public void addsAuthorizeHeader() throws Exception {
        RecordingHttpHandler server = recordingHttpHandler();
        HttpClient client = new BasicHttpClient(server, credential("dan", "correct"));
        client.handle(Request.get("/"));
        assertThat(server.lastRequest().headers().getValue(HttpHeaders.AUTHORIZATION), is("Basic ZGFuOmNvcnJlY3Q="));
    }
}