package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.Request;
import org.junit.Test;

import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.handlers.RecordingHttpHandler.recordingHttpHandler;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static com.googlecode.utterlyidle.handlers.RoutingClient.allTrafficTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RoutingClientTest {
    @Test
    public void canRewriteUrl() throws Exception {
        RecordingHttpHandler recordingHttpHandler = recordingHttpHandler(returnsResponse("ignore me"));
        RoutingClient routingClient = allTrafficTo(recordingHttpHandler, "localhost:666");
        routingClient.handle(Request.get("http://some.server.com:1234/some/path?query=123"));
        assertThat(recordingHttpHandler.requests().head().uri(), is(Uri.uri("http://localhost:666/some/path?query=123")));
    }
}
