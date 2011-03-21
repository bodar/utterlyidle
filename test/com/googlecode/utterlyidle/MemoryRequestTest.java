package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.Url;
import org.junit.Test;

import javax.ws.rs.HttpMethod;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Requests.request;
import static com.googlecode.utterlyidle.ResourcePath.resourcePath;
import static com.googlecode.utterlyidle.ResourcePath.resourcePathOf;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class MemoryRequestTest {



    @Test
    public void shouldBeReversibleToRawMessage() {
        assertThat(post("http://www.youtube.com/watch?v=606eK4abteQ")
                .accepting("text/html")
                .withForm("chups", "nah bru")
                .withForm("plinkton", "nom")
                .withHeader("Cookie", "size=diciptive")
                .withHeader("Referer", "http://google.com").
                        build().toString(),
                   is(
                           "POST http://www.youtube.com/watch?v=606eK4abteQ HTTP/1.1\r\n" +
                                   "Accept: text/html\r\n" +
                                   "Content-Type: application/x-www-form-urlencoded\r\n" +
                                   "Cookie: size=diciptive\r\n" +
                                   "Referer: http://google.com\r\n" +
                                   "Content-length: 26\r\n" +
                                   "\r\n" +
                                   "chups=nah+bru&plinkton=nom"
                   ));
    }

    @Test
    public void toStringCanBeCalledMultipleTimes() throws Exception {
        Request request = request("GET", url("smoosh"), HeaderParameters.headerParameters(), "some input".getBytes());

        assertThat(request.toString(), containsString("some input"));
        assertThat(request.toString(), containsString("some input"));
    }

    @Test
    public void shouldNotHoldOnToOldQueryParametersAfterUrlIsChanged() {
        Request request = get("http://www.google.com?q=handies+that+look+like+gandhis").build();

        assertThat(request.query().getValue("q"), is("handies that look like gandhis"));

        request.url(url("http://www.google.com?q=cheeses+that+look+like+jesus"));
        
        assertThat(request.query().getValue("q"), is("cheeses that look like jesus"));
    }

    @Test
    public void shouldSupportRetrievingResourcePath() throws Exception {
        BasePath basePath = basePath("/foobar/");
        Request request = get("http://www.myserver.com/foobar/spaz").build();

        assertThat(resourcePathOf(request, basePath), is(resourcePath("spaz")));

        Request anotherRequest = get("http://www.myserver.com/foobar").build();
        assertThat(resourcePathOf(anotherRequest, basePath), is(resourcePath("")));

    }

    private Request createRequestWith(Url url) {
        return new MemoryRequest(HttpMethod.GET, url, HeaderParameters.headerParameters(), "foo".getBytes());
    }

}
