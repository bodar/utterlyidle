package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.rendering.exceptions.LastExceptions;
import com.googlecode.utterlyidle.rendering.exceptions.StoredException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.time.Dates.LEXICAL;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.IF_NONE_MATCH;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;
import static com.googlecode.utterlyidle.RequestBuilder.delete;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.head;
import static com.googlecode.utterlyidle.RequestBuilder.patch;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.RequestBuilder.put;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.handlers.ClientHttpHandlerTest.handle;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

public abstract class ServerContract<T extends Server> {
    protected T server;

    protected abstract Class<T> server() throws Exception;

    @Before
    public void start() throws Exception {
        server = cast(application(HelloWorldApplication.class).start(defaultConfiguration().basePath(basePath("base/path")).serverClass(server())));
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
    public void shouldCaptureStreamingExceptions() throws Exception {
        final Sequence<StoredException> exceptions = sequence(server.application().applicationScope().get(LastExceptions.class));
        assertThat(exceptions.size(), is(0));
        handle(get("stream-exception"), server).toString();
        assertThat(exceptions.size(), is(1));
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
        assertThat(response.entity().length(), CoreMatchers.is(some(0)));
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
        Response response = handle(get("helloworld/xff").header(X_FORWARDED_FOR, "sky.com"), server);

        String result = response.entity().toString();

        assertThat(result, is("sky.com"));
    }

    @Test
    public void setXForwardedProtoIfRequestDoesntHaveOne() throws Exception {
        Response response = handle(get("helloworld/x-forwarded-proto"), server);

        String result = response.entity().toString();

        assertThat(result, startsWith("http"));
    }

    @Test
    public void preservesXForwardedProtoIfRequestHasOne() throws Exception {
        Response response = handle(get("helloworld/x-forwarded-proto").header(X_FORWARDED_PROTO, "https"), server);

        String result = response.entity().toString();

        assertThat(result, is("https"));
    }

    @Test
    public void handlesGets() throws Exception {
        Response response = handle(get("helloworld/queryparam").query("name", "foo"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello foo"));
    }

    @Test
    public void handlesPatch() throws Exception {
        Response response = handle(patch("helloworld/patch").query("name", "James"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello James"));
    }

    @Test
    public void handlesPosts() throws Exception {
        Response response = handle(post("helloworld/formparam").form("name", "fred"), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello fred"));
    }

    @Test
    public void mapsRequestHeaders() throws Exception {
        Response response = handle(get("helloworld/headerparam").accepting(WILDCARD).header("name", "bar"), server);

        assertThat(response.entity().toString(), is("Hello bar"));
    }

    @Test
    public void handlesTheAnyCatchAllHttpVerb() throws Exception {
        assertThat(handle(get("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(post("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(put("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(delete("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(head("any"), server).headers().getValue("x-custom-header"), is("smile"));
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
        Response response = handle(get("echoquery").query("a", "first").query("a", "second").accepting(WILDCARD), server);

        String result = response.entity().toString();

        assertThat(result, containsString("first"));
        assertThat(result, containsString("second"));
    }

    @Test
    public void retainsOrderOfQueryParameters() throws Exception {
        Response response = handle(get("echoquery").query("a", "1").query("b", "2").query("a", "3").query("b", "4").accepting(WILDCARD), server);

        assertThat(response.entity().toString(), is("?a=1&b=2&a=3&b=4"));
    }

    @Test
    public void willPrintStackTraceAsPlainText() throws Exception {
        Response response = handle(get("goesbang").query("exceptionMessage", "goes_bang").accepting(WILDCARD), server);

        assertThat(response.status(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(response.entity().toString(), allOf(containsString("Exception"), containsString("goes_bang")));
    }

    @Test
    public void canHandleOptionalDate() throws Exception {
        assertThat(handle(get("optionalDate"), server).entity().toString(), is("no date"));
        assertThat(handle(get("optionalDate?date="), server).entity().toString(), is("no date"));
        assertThat(handle(get("optionalDate?date=" + LEXICAL().format(Dates.date(1974, 10, 29))), server).entity().toString(), is("19741029000000000"));
    }
}