package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.Request.Builder.query;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RelativeUrlHandlerTest {
    RelativeUrlHandler handler = new RelativeUrlHandler(req -> null);

    @Test
    public void supportsRelativeUrlWithDotSegments() throws Exception {
        String fullyQualified = "http://localhost:1234/foo/bar";
        handler.handle(get(fullyQualified));
        handler.handle(get("../baz/index.html"));
        assertThat(handler.currentUri().toString(), is("http://localhost:1234/baz/index.html"));
    }

    @Test
    public void ifTheFirstRequestIsRelativeTreatItRelativeToTheRoot() throws Exception {
        String relative = "foo";
        handler.handle(get(relative));
        assertThat(handler.currentUri().toString(), is("/foo"));
    }

    @Test
    public void preservesSchemeAndAuthority() throws Exception {
        String fullyQualified = "http://localhost:1234/foo/bar?q=123";
        handler.handle(get(fullyQualified));
        handler.handle(Request.post("/bar"));
        assertThat(handler.currentUri().toString(), is("http://localhost:1234/bar"));
    }

    @Test
    public void preserveQueryParameters() throws Exception {
        String urlWithQueryParameter = "/foo/bar?q=123";
        handler.handle(get(urlWithQueryParameter));
        handler.handle(Request.post(""));
        assertThat(handler.currentUri().toString(), is(urlWithQueryParameter));
    }

    @Test
    public void preserveAbsolutePath() throws Exception {
        handler.handle(get("/foo/bar"));
        assertThat(handler.currentUri().toString(), is("/foo/bar"));
        handler.handle(get("baz"));
        assertThat(handler.currentUri().toString(), is("/foo/baz"));
    }

    @Test
    public void rebuildsRelativeUrlWithQuery() throws Exception {
        handler.handle(get("/foo/bar"));
        handler.handle(get("", query("some", "param")));
        assertThat(handler.currentUri(), is(uri("/foo/bar?some=param")));
    }

    @Test
    public void shouldAddQueryParameterWhenPostToNoUrl() throws Exception {
        handler.handle(get("/foo/bar"));
        handler.handle(Request.post("", query("some", "param")));
        assertThat(handler.currentUri(), is(uri("/foo/bar?some=param")));
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
