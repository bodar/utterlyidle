package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.handlers.ClientHttpHandlerTest.handle;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

public abstract class ServerContract {
    protected Server server;
    protected abstract Class<? extends Server> server() throws Exception;

    @Before
    public void start() throws Exception {
        server = new ServerActivator(new HelloWorldApplication(), defaultConfiguration().serverClass(server())).call();
    }

    @After
    public void stop() throws Exception {
        server.close();
    }

    @Test
    public void shouldCorrectlyHandlerEtagsAndNotModified() throws Exception {
        Response response = handle(get("etag"), server);

        assertThat(response.status(), Matchers.is(Status.OK));
        assertThat(response.header(ETAG), CoreMatchers.is("\"900150983cd24fb0d6963f7d28e17f72\""));
        assertThat(response.header(CONTENT_LENGTH), CoreMatchers.is("3"));
    }

    @Test
    public void shouldSetServerUrlIntoRequestScopeSoThatAllRedirectsAreFullyQualified() throws Exception {
        Response response = handle(get("helloworld/redirect"), server);

        assertThat(response.status(), Matchers.is(Status.SEE_OTHER));
        assertThat(response.header(LOCATION), CoreMatchers.startsWith(server.uri().toString()));
    }

    @Test
    public void setXForwardedForIfRequestDoesntHaveOne() throws Exception {
        Response response = handle(get("helloworld/xff"), server);

        String result = new String(response.bytes());

        assertThat(result, startsWith("127.0."));
    }

    @Test
    public void preservesXForwardedForIfRequestHasOne() throws Exception {
        Response response = handle(get("helloworld/xff").withHeader(X_FORWARDED_FOR, "sky.com"), server);

        String result = new String(response.bytes());

        assertThat(result, is("sky.com"));
    }

    @Test
    public void handlesGets() throws Exception {
        Response response = handle(get("helloworld/queryparam").withQuery("name", "foo"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(new String(response.bytes()), is("Hello foo"));
    }

    @Test
    public void handlesPosts() throws Exception {
        Response response = handle(post("helloworld/formparam").withForm("name", "fred"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(new String(response.bytes()), is("Hello fred"));
    }

    @Test
    public void mapsRequestHeaders() throws Exception {
        Response response = handle(get("helloworld/headerparam").accepting(WILDCARD).withHeader("name", "bar"), server);

        assertThat(new String(response.bytes()), is("Hello bar"));
    }

    @Test
    public void mapsResponseHeaders() throws Exception {
        Response response = handle(get("helloworld/inresponseheaders?name=mike").accepting(WILDCARD), server);

        assertThat(response.header("greeting"), is("Hello mike"));
    }

    @Test
    public void mapsStatusCode() throws Exception {
        Response response = handle(get("doesnotexist"), server);

        assertThat(response.status(), is(NOT_FOUND));
    }

    @Test
    public void canHandleMultiValueQueryParameters() throws Exception {
        Response response = handle(get("echoquery").withQuery("a", "first").withQuery("a", "second").accepting(WILDCARD), server);

        String result = new String(response.bytes());

        assertThat(result, containsString("first"));
        assertThat(result, containsString("second"));
    }

    @Test
    public void retainsOrderOfQueryParameters() throws Exception {
        Response response = handle(get("echoquery").withQuery("a", "1").withQuery("b", "2").withQuery("a", "3").withQuery("b", "4").accepting(WILDCARD), server);

        assertThat(new String(response.bytes()), is("?a=1&b=2&a=3&b=4"));
    }

    @Test
    public void willPrintStackTraceAsPlainText() throws Exception {
        Response response = handle(get("goesbang").withQuery("exceptionMessage", "goes_bang").accepting(WILDCARD), server);

        assertThat(response.status(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(new String(response.bytes()), allOf(containsString("Exception"), containsString("goes_bang")));
    }
}
