package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.predicates.Predicates;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.ClientConfiguration.Builder;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.rendering.exceptions.LastExceptions;
import com.googlecode.utterlyidle.rendering.exceptions.StoredException;
import com.googlecode.utterlyidle.ssl.SecureString;
import com.googlecode.utterlyidle.ssl.SecureStringTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.InputStream;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.time.Dates.LEXICAL;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.ClientConfiguration.Builder.clientConfiguration;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.IF_NONE_MATCH;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;
import static com.googlecode.utterlyidle.Parameters.Builder.add;
import static com.googlecode.utterlyidle.Request.Builder.accept;
import static com.googlecode.utterlyidle.Request.Builder.form;
import static com.googlecode.utterlyidle.Request.Builder.query;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.handlers.ClientHttpHandlerTest.handle;
import static com.googlecode.utterlyidle.ssl.SSL.keyStore;
import static com.googlecode.utterlyidle.ssl.SSL.sslContext;
import static com.googlecode.utterlyidle.ssl.SecureString.secureString;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assume.assumeTrue;

public abstract class ServerContract<T extends Server> {
    protected T server;

    protected abstract Class<T> server() throws Exception;

    protected boolean isServletBased() {
        return false;
    }

    @Before
    public void start() throws Exception {
        server = configureServer(defaultConfiguration());
    }

    private T configureServer(final ServerConfiguration configuration) throws Exception {
        return cast(application(HelloWorldApplication.class).start(configuration.basePath(basePath("base/path")).serverClass(server())));
    }

    @After
    public void stop() throws Exception {
        server.close();
    }

    @Test
    public void detectsEmptyInputStreams() throws Exception {
        assumeTrue(isServletBased());
        Response response = handle(Request.get("empty"), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("true"));
    }

    @Test
    public void handlesChunking() throws Exception {
        Response response = handle(Request.get("chunk"), server);

        assertThat(response.entity().toString(), response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("chunk"));
    }

    @Test
    public void shouldCaptureStreamingExceptions() throws Exception {
        final Sequence<StoredException> exceptions = sequence(server.application().applicationScope().get(LastExceptions.class));
        assertThat(exceptions.size(), is(0));
        handle(Request.get("stream-exception"), server).toString();
        assertThat(exceptions.size(), is(1));
    }

    @Test
    public void shouldCorrectlyHandlerEtagsAndNotModified() throws Exception {
        Response responseWithEtag = handle(Request.get("etag"), server);

        assertThat(responseWithEtag.status(), Matchers.is(Status.OK));
        assertThat(responseWithEtag.header(ETAG).get(), CoreMatchers.is("\"900150983cd24fb0d6963f7d28e17f72\""));
        assertThat(responseWithEtag.header(CONTENT_LENGTH).get(), CoreMatchers.is("3"));

        Response response = handle(Request.get("etag", HttpMessage.Builder.header(IF_NONE_MATCH, responseWithEtag.header(ETAG).get())), server);

        assertThat(response.status(), Matchers.is(Status.NOT_MODIFIED));
        assertThat(response.header(CONTENT_LENGTH).filter(not(Predicates.is("0"))), CoreMatchers.is(none(String.class)));
    }

    @Test
    public void shouldSetServerUrlIntoRequestScopeSoThatAllRedirectsAreFullyQualified() throws Exception {
        Response response = handle(Request.get("helloworld/redirect"), server);

        assertThat(response.status(), Matchers.is(Status.SEE_OTHER));
        assertThat(response.header(LOCATION).get(), CoreMatchers.startsWith(server.uri().toString()));
    }

    @Test
    public void setXForwardedForIfRequestDoesntHaveOne() throws Exception {
        Response response = handle(Request.get("helloworld/xff"), server);

        String result = response.entity().toString();

        assertThat(result, startsWith("127.0."));
    }

    @Test
    public void preservesXForwardedForIfRequestHasOne() throws Exception {
        Response response = handle(Request.get("helloworld/xff", HttpMessage.Builder.header(X_FORWARDED_FOR, "sky.com")), server);

        String result = response.entity().toString();

        assertThat(result, is("sky.com"));
    }

    @Test
    public void setXForwardedProtoIfRequestDoesntHaveOne() throws Exception {
        Response response = handle(Request.get("helloworld/x-forwarded-proto"), server);

        String result = response.entity().toString();

        assertThat(result, startsWith("http"));
    }

    @Test
    public void preservesXForwardedProtoIfRequestHasOne() throws Exception {
        Response response = handle(Request.get("helloworld/x-forwarded-proto", HttpMessage.Builder.header(X_FORWARDED_PROTO, "https")), server);

        String result = response.entity().toString();

        assertThat(result, is("https"));
    }

    @Test
    public void handlesOptions() throws Exception {
        Response response = handle(Request.options("helloworld/options", query("name", "foo")), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello foo"));
    }

    @Test
    public void handlesGets() throws Exception {
        Response response = handle(Request.get("helloworld/queryparam", query("name", "foo")), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello foo"));
    }

    @Test
    public void handlesPatch() throws Exception {
        Response response = handle(Request.patch("helloworld/patch", query("name", "James")), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello James"));
    }

    @Test
    public void handlesPosts() throws Exception {
        Response response = handle(Request.post("helloworld/formparam", form("name", "fred")), server);

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello fred"));
    }

    @Test
    public void mapsRequestHeaders() throws Exception {
        Response response = handle(Request.get("helloworld/headerparam", accept(WILDCARD), HttpMessage.Builder.header("name", "bar")), server);

        assertThat(response.entity().toString(), is("Hello bar"));
    }

    @Test
    public void handlesTheAnyCatchAllHttpVerb() throws Exception {
        assertThat(handle(Request.get("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(Request.post("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(Request.put("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(Request.delete("any"), server).entity().toString(), is("Hello everyone"));
        assertThat(handle(Request.head("any"), server).headers().getValue("x-custom-header"), is("smile"));
    }

    @Test
    public void mapsResponseHeaders() throws Exception {
        Response response = handle(Request.get("helloworld/inresponseheaders?name=mike", Request.Builder.accept(WILDCARD)), server);

        assertThat(response.header("greeting").get(), is("Hello mike"));
    }

    @Test
    public void mapsStatusCode() throws Exception {
        Response response = handle(Request.get("doesnotexist"), server);

        assertThat(response.status(), is(NOT_FOUND));
    }

    @Test
    public void canHandleMultiValueQueryParameters() throws Exception {
        Response response = handle(Request.get("echoquery", query(add("a", "first"), add("a", "second")), accept(WILDCARD)), server);

        String result = response.entity().toString();

        assertThat(result, containsString("first"));
        assertThat(result, containsString("second"));
    }

    @Test
    public void retainsOrderOfQueryParameters() throws Exception {
        Response response = handle(Request.get("echoquery", query(add("a", "1"), add("b", "2"), add("a", "3"), add("b", "4")), accept(WILDCARD)), server);

        assertThat(response.entity().toString(), is("a=1&b=2&a=3&b=4"));
    }

    @Test
    public void willPrintStackTraceAsPlainText() throws Exception {
        Response response = handle(Request.get("goesbang", query("exceptionMessage", "goes_bang"), accept(WILDCARD)), server);

        assertThat(response.status(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(response.entity().toString(), allOf(containsString("Exception"), containsString("goes_bang")));
    }

    @Test
    public void canHandleOptionalDate() throws Exception {
        assertThat(handle(Request.get("optionalDate"), server).entity().toString(), is("no date"));
        assertThat(handle(Request.get("optionalDate?date="), server).entity().toString(), is("no date"));
        assertThat(handle(Request.get("optionalDate?date=" + LEXICAL().format(Dates.date(1974, 10, 29))), server).entity().toString(), is("19741029000000000"));
    }

    @Test
    public void supportsHttps() throws Exception {
        try (InputStream resource = SecureStringTest.class.getResourceAsStream("localhost.jks");
             SecureString password = secureString('p', 'a', 's', 's', 'w', 'o', 'r', 'd')) {
            SSLContext context = sslContext(keyStore(password, resource), password);
            server.close();
            server = configureServer(defaultConfiguration().sslContext(context));
            Response response = handle(new ClientHttpHandler(clientConfiguration(Builder.sslContext(context))), Request.get("helloworld/x-forwarded-proto"), server);

            assertThat(response.status(), is(Status.OK));
            assertThat(response.entity().toString(), is(Protocol.HTTPS));
        }
    }

}