package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RecordingHttpHandler;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.authentication.Credential.credential;
import static com.googlecode.utterlyidle.handlers.RecordingHttpHandler.recordingHttpHandler;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicHttpClientTest {
    @Test
    public void addsAuthorizeHeader() throws Exception {
        RecordingHttpHandler server = recordingHttpHandler(returns(response(OK).build()));
        HttpClient client = new BasicHttpClient(server, credential("dan", "correct"));
        client.handle(get("/").build());
        assertThat(server.lastRequest().headers().getValue(HttpHeaders.AUTHORIZATION), is("Basic ZGFuOmNvcnJlY3Q="));
    }
}