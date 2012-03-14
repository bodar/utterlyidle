package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.IF_NONE_MATCH;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Response.methods.header;
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
        server = application(HelloWorldApplication.class).start(defaultConfiguration().serverClass(server()));
    }

    @After
    public void stop() throws Exception {
        server.close();
    }

    @Test
    public void handlesChunking() throws Exception {
        Response response = handle(get("chunk"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("chunk"));
    }

    @Test
    public void shouldCorrectlyHandlerEtagsAndNotModified() throws Exception {
        Response responseWithEtag = handle(get("etag"), server);

        assertThat(responseWithEtag.status(), Matchers.is(Status.OK));
        assertThat(header(responseWithEtag, ETAG), CoreMatchers.is("\"900150983cd24fb0d6963f7d28e17f72\""));
        assertThat(header(responseWithEtag, CONTENT_LENGTH), CoreMatchers.is("3"));

        Response response = handle(get("etag").header(IF_NONE_MATCH, header(responseWithEtag, ETAG)), server);

        assertThat(response.status(), Matchers.is(Status.NOT_MODIFIED));
        assertThat(header(response, CONTENT_LENGTH), CoreMatchers.is("0"));
        assertThat(response.entity().asBytes().length, CoreMatchers.is(0));
    }

    @Test
    public void shouldSetServerUrlIntoRequestScopeSoThatAllRedirectsAreFullyQualified() throws Exception {
        Response response = handle(get("helloworld/redirect"), server);

        assertThat(response.status(), Matchers.is(Status.SEE_OTHER));
        assertThat(header(response, LOCATION), CoreMatchers.startsWith(server.uri().toString()));
    }

    @Test
    public void setXForwardedForIfRequestDoesntHaveOne() throws Exception {
        Response response = handle(get("helloworld/xff"), server);

        String result = response.entity().toString();

        assertThat(result, startsWith("127.0."));
    }

    @Test
    public void preservesXForwardedForIfRequestHasOne() throws Exception {
        Response response = handle(get("helloworld/xff").withHeader(X_FORWARDED_FOR, "sky.com"), server);

        String result = response.entity().toString();

        assertThat(result, is("sky.com"));
    }

    @Test
    public void handlesGets() throws Exception {
        Response response = handle(get("helloworld/queryparam").withQuery("name", "foo"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello foo"));
    }

    @Test
    public void handlesPosts() throws Exception {
        Response response = handle(post("helloworld/formparam").withForm("name", "fred"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello fred"));
    }

    @Test
    public void mapsRequestHeaders() throws Exception {
        Response response = handle(get("helloworld/headerparam").accepting(WILDCARD).withHeader("name", "bar"), server);

        assertThat(response.entity().toString(), is("Hello bar"));
    }

    @Test
    public void mapsResponseHeaders() throws Exception {
        Response response = handle(get("helloworld/inresponseheaders?name=mike").accepting(WILDCARD), server);

        assertThat(header(response, "greeting"), is("Hello mike"));
    }

    @Test
    public void mapsStatusCode() throws Exception {
        Response response = handle(get("doesnotexist"), server);

        assertThat(response.status(), is(NOT_FOUND));
    }

    @Test
    public void canHandleMultiValueQueryParameters() throws Exception {
        Response response = handle(get("echoquery").withQuery("a", "first").withQuery("a", "second").accepting(WILDCARD), server);

        String result = response.entity().toString();

        assertThat(result, containsString("first"));
        assertThat(result, containsString("second"));
    }

    @Test
    public void retainsOrderOfQueryParameters() throws Exception {
        Response response = handle(get("echoquery").withQuery("a", "1").withQuery("b", "2").withQuery("a", "3").withQuery("b", "4").accepting(WILDCARD), server);

        assertThat(response.entity().toString(), is("?a=1&b=2&a=3&b=4"));
    }

    @Test
    public void willPrintStackTraceAsPlainText() throws Exception {
        Response response = handle(get("goesbang").withQuery("exceptionMessage", "goes_bang").accepting(WILDCARD), server);

        assertThat(response.status(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(response.entity().toString(), allOf(containsString("Exception"), containsString("goes_bang")));
    }
}
