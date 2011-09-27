package com.googlecode.utterlyidle.html;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RelativeUrlHandlerTest {

    @Test
    public void preserveAbsolutePath() throws Exception {
        RelativeUrlHandler relativeUrlHandler = new RelativeUrlHandler(doNothingHttpHandler());
        relativeUrlHandler.handle(get("/foo/bar").build());
        assertThat(relativeUrlHandler.getCurrentUri().toString(), is("/foo/bar"));
        relativeUrlHandler.handle(get("baz").build());
        assertThat(relativeUrlHandler.getCurrentUri().toString(), is("/foo/baz"));
    }

    private HttpHandler doNothingHttpHandler() {
        return new HttpHandler() {
            public Response handle(Request request) throws Exception {
                return null;
            }
        };
    }
}
