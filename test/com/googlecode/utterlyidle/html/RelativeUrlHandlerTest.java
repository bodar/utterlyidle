package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.http.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.http.Uri.uri;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RelativeUrlHandlerTest {
    RecordRequestHandler delegate = new RecordRequestHandler();
    RelativeUrlHandler handler = new RelativeUrlHandler(delegate);

    @Test
    public void ifTheFirstRequestIsRelativeTreatItRelativeToTheRoot() throws Exception {
        String relative = "foo";
        handler.handle(get(relative).build());
        assertThat(delegate.lastUriReceived.toString(), is("/foo"));
    }

    @Test
    public void preservesSchemeAndAuthority() throws Exception {
        String fullyQualified = "http://localhost:1234/foo/bar?q=123";
        handler.handle(get(fullyQualified).build());
        handler.handle(post("/bar").build());
        assertThat(delegate.lastUriReceived.toString(), is("http://localhost:1234/bar"));
    }

    @Test
    public void preserveQueryParameters() throws Exception {
        String urlWithQueryParameter = "/foo/bar?q=123";
        handler.handle(get(urlWithQueryParameter).build());
        handler.handle(post("").build());
        assertThat(handler.getCurrentUri().toString(), is(urlWithQueryParameter));
    }

    @Test
    public void preserveAbsolutePath() throws Exception {
        handler.handle(get("/foo/bar").build());
        assertThat(handler.getCurrentUri().toString(), is("/foo/bar"));
        handler.handle(get("baz").build());
        assertThat(handler.getCurrentUri().toString(), is("/foo/baz"));
    }

    @Test
    public void rebuildsRelativeUrlWithQuery() throws Exception {
        handler.handle(get("/foo/bar").build());
        handler.handle(get("").query("some", "param").build());
        assertThat(delegate.lastUriReceived, is(uri("/foo/bar?some=param")));
    }

    @Test
    public void shouldAddQueryParameterWhenPostToNoUrl() throws Exception {
        handler.handle(get("/foo/bar").build());
        handler.handle(post("").query("some", "param").build());
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
