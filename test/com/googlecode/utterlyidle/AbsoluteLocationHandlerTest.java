package com.googlecode.utterlyidle;

import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.seeOther;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AbsoluteLocationHandlerTest {
    @Test
    public void makesRelativeLocationsAbsolute() throws Exception {
        assertLocationIsCorrectlyModified("foo", "/foo");
        assertLocationIsCorrectlyModified("foo/bar", "/foo/bar");
    }

    @Test
    public void doesNotModifyAbsoluteLocations() throws Exception {
        assertLocationIsCorrectlyModified("/bar/bob", "/bar/bob");
        assertLocationIsCorrectlyModified("http://server/bar/bob", "http://server/bar/bob");
    }

    private void assertLocationIsCorrectlyModified(final String originalLocation, final String finalLocation) throws Exception {
        Response response = new AbsoluteLocationHandler(returnResponse(seeOther(originalLocation))).
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
