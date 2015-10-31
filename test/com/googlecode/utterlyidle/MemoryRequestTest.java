package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.Request.Builder.accept;
import static com.googlecode.utterlyidle.Request.Builder.form;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.Requests.request;
import static com.googlecode.utterlyidle.ResourcePath.resourcePath;
import static com.googlecode.utterlyidle.ResourcePath.resourcePathOf;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class MemoryRequestTest {
    @Test
    public void shouldBeReversibleToRawMessage() {
        assertThat(Request.post("http://www.youtube.com/watch?v=606eK4abteQ",
                        accept("text/html"),
                        form("chups", "nah bru"),
                        form("plinkton", "nom"),
                        HttpMessage.Builder.header("Cookie", "size=diciptive"),
                        HttpMessage.Builder.header("Referer", "http://google.com")).toString(),
                is(
                        "POST http://www.youtube.com/watch?v=606eK4abteQ HTTP/1.1\r\n" +
                                "Accept: text/html\r\n" +
                                "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n" +
                                "Cookie: size=diciptive\r\n" +
                                "Referer: http://google.com\r\n" +
                                "Content-Length: 26\r\n" +
                                "\r\n" +
                                "chups=nah+bru&plinkton=nom"
                ));
    }

    @Test
    public void toStringCanBeCalledMultipleTimes() throws Exception {
        Request request = request("GET", uri("smoosh"), HeaderParameters.headerParameters(), "some input".getBytes());

        assertThat(request.toString(), containsString("some input"));
        assertThat(request.toString(), containsString("some input"));
    }

    @Test
    public void shouldSupportRetrievingResourcePath() throws Exception {
        assertThat(resourcePathOf(Request.get("http://www.myserver.com/foobar/spaz")), is(resourcePath("/foobar/spaz")));
    }

    @Test
    public void shouldSupportEquals() {
        assertEquals(Request.get("http://www.google.com"), Request.get("http://www.google.com"));
    }

}
