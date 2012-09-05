package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RelativeUrlHandlerTest {

    RecordRequestHandler delegate = new RecordRequestHandler();

    @Test
    public void preserveQueryParameters() throws Exception {
        RelativeUrlHandler relativeUrlHandler = new RelativeUrlHandler(delegate);
        String urlWithQueryParameter = "/foo/bar?q=123";
        relativeUrlHandler.handle(get(urlWithQueryParameter).build());
        relativeUrlHandler.handle(post("").build());
        assertThat(relativeUrlHandler.getCurrentUri().toString(), is(urlWithQueryParameter));
    }

    @Test
    public void preserveAbsolutePath() throws Exception {
        RelativeUrlHandler relativeUrlHandler = new RelativeUrlHandler(delegate);
        relativeUrlHandler.handle(get("/foo/bar").build());
        assertThat(relativeUrlHandler.getCurrentUri().toString(), is("/foo/bar"));
        relativeUrlHandler.handle(get("baz").build());
        assertThat(relativeUrlHandler.getCurrentUri().toString(), is("/foo/baz"));
    }

    @Test
    public void rebuildsRelativeUrlWithQuery() throws Exception {
        RelativeUrlHandler relativeUrlHandler = new RelativeUrlHandler(delegate);
        relativeUrlHandler.handle(get("/foo/bar").build());
        relativeUrlHandler.handle(get("").query("some", "param").build());
        assertThat(delegate.lastUriReceived, is(uri("/foo/bar?some=param")));
    }

    private static class RecordRequestHandler implements HttpHandler {

        public Uri lastUriReceived;
        @Override
        public Response handle(Request request) throws Exception {
            lastUriReceived=request.uri();
            return null;
        }
    }
}
