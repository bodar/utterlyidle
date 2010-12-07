package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.junit.Ignore;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class SiteMeshHandlerTest {
    @Test
    @Ignore
    public void shouldDecorateHtml() throws Exception{
        Response response = Response.response();
        RequestHandler hello = write("Hello");
        RequestHandler decorator = new SiteMeshHandler(hello);

        decorator.handle(get(null).build(), response);

        assertThat(response.toString(), containsString("Hello World!"));
    }

    private RequestHandler write(final String value) {
        return new RequestHandler() {
            public void handle(Request request, Response response) throws Exception {
                response.write(value);
                response.flush();
            }
        };
    }
}
