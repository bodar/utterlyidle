package com.googlecode.utterlyidle;

import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.seeOther;
import static com.googlecode.utterlyidle.ServerUrl.serverUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AbsoluteLocationHandlerTest {
    @Test
    public void makesRelativeLocationsAbsolute() throws Exception {
        assertLocationIsCorrectlyModified("foo", "http://mayhost:8080/foo");
        assertLocationIsCorrectlyModified("foo/bar?a=b", "http://mayhost:8080/foo/bar?a=b");
        assertLocationIsCorrectlyModified("/bar/bob", "http://mayhost:8080/bar/bob");
    }

    @Test
    public void doesNotModifyAbsoluteLocations() throws Exception {
        assertLocationIsCorrectlyModified("http://mayhost:8080/bar/bob", "http://mayhost:8080/bar/bob");
    }

    private void assertLocationIsCorrectlyModified(final String originalLocation, final String finalLocation) throws Exception {
        Response response = new AbsoluteLocationHandler(returnResponse(seeOther(originalLocation)), serverUrl("http://mayhost:8080/")).
                handle(get("").build());
        assertThat(response.header(HttpHeaders.LOCATION), is(finalLocation));
        assertThat(response.status(), Matchers.is(Status.SEE_OTHER));
    }

    private HttpHandler returnResponse(final Response response) {
        return new HttpHandler() {
            public Response handle(Request request) throws Exception {
                return response;
            }
        };
    }
}
