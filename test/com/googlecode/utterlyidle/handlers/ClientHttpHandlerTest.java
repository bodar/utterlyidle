package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Debug;
import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Streams;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.io.URLs;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.io.Zip;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.totallylazy.matchers.NumberMatcher.greaterThan;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.Entities.inputStreamOf;
import static com.googlecode.utterlyidle.HttpHeaders.LAST_MODIFIED;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.RequestBuilder.put;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.handlers.RequestTimeout.requestTimeout;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class ClientHttpHandlerTest {
    @Test
    public void canCloseClient() throws Exception {
        ClientHttpHandler client = new ClientHttpHandler();
        final Response response = handle(client, get(uri("chunk")), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(response.headers().contains(HttpHeaders.CONTENT_LENGTH), is(false));
        assertThat(response.entity().inputStream().read(), is(greaterThan(0)));

        client.close();

        try {
            response.entity().inputStream().read();
            fail("Should have closed");
        } catch (IOException e) {

        }
    }

    @Test(timeout = 500)
    public void correctlyHandlesStreamedRequest() throws Exception {
        final Response response = handle(put("echo").entity(inputStreamOf("Hello")), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(response.headers().contains(HttpHeaders.CONTENT_LENGTH), is(false));
        assertThat(response.entity().toString(), is("Hello"));
    }

    @Test(timeout = 500)
    public void correctlyHandlesStreamedResponse() throws Exception {
        final Response response = handle(get(uri("primes")), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(response.headers().contains(HttpHeaders.CONTENT_LENGTH), is(false));
        assertThat(Strings.lines(response.entity().inputStream()).take(3), is(sequence("2", "3", "5")));
    }

    @Test
    public void canPutAString() throws Exception {
        Response response = handle(put("echo").entity("Hello"), server);
        assertThat(response.entity().toString(), Matchers.is("Hello"));
    }

    @Test
    public void canPutByteArray() throws Exception {
        Response response = handle(put("echo").entity(bytes("Hello")), server);
        assertThat(response.entity().toString(), Matchers.is("Hello"));
    }

    @Test
    public void correctlyHandlesChunkedTransferEncoding() throws Exception {
        Response response = handle(get(uri("chunk")), server);
        assertThat(response.entity().isStreaming(), is(true));
        assertThat(response.headers().contains(HttpHeaders.TRANSFER_ENCODING), is(false));
        assertThat(response.headers().contains(HttpHeaders.CONTENT_LENGTH), is(false));
    }

    @Test
    public void correctlyHandlesTimeouts() throws Exception {
        Response response = handle(10, get("slow"), server);
        assertThat(response.status(), is(Status.CLIENT_TIMEOUT));
    }

    @Test
    public void correctlyHandlesRequestTimeout() throws Exception {
        Response response = handle(new ClientHttpHandler(requestTimeout(10)), get("slow"), server);
        assertThat(response.status(), is(Status.CLIENT_TIMEOUT));
    }

    @Test
    public void correctlyHandlesGlobalTimeouts() throws Exception {
        Response response = handle(new TimeoutClient(10, new ClientHttpHandler(0)), get("slow"), server);
        assertThat(response.status(), is(Status.CLIENT_TIMEOUT));
    }

    @Test
    public void correctlyHandlesConnectionRefused() throws Exception {
        Response response = new ClientHttpHandler().handle(get(uri("http://127.0.0.1:0/")).build());
        assertThat(response.status(), is(Status.CONNECTION_REFUSED));
    }

    @Test
    public void correctlyHandlesANotFoundFileUrl() throws Exception {
        URL resource = URLs.url("file:///bob");
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(resource.toString()).build());
        assertThat(response.status(), is(Status.NOT_FOUND));
    }

    @Test
    public void supportsPutWithFileUrls() throws Exception {
        File file = new File(Files.temporaryDirectory(), Files.randomFilename());
        file.deleteOnExit();
        assertThat(file.exists(), is(false));
        HttpClient client = new ClientHttpHandler();
        Date lastModified = Dates.date(2001, 1, 1);
        Uri uri = uri(file);
        String content = "hairy monkey";
        Response response = client.handle(put(uri).header(LAST_MODIFIED, lastModified).entity(content).build());
        assertThat(response.status(), is(Status.CREATED));
        assertThat(header(response, HttpHeaders.LOCATION), is(uri.toString()));
        assertThat(file.exists(), is(true));
        assertThat(file.lastModified(), is(lastModified.getTime()));
        assertThat(Strings.toString(file), is(content));
    }

    @Test
    public void supportsLastModifiedOnFileUrls() throws Exception {
        File file = Files.temporaryFile();
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(uri(file)).build());
        assertThat(response.status(), is(Status.OK));
        assertThat(header(response, LAST_MODIFIED), is(Dates.RFC822().format(Dates.date(file.lastModified()))));
    }

    @Test
    public void supportsLastModifiedOnJarUrls() throws Exception {
        File parentTempDir = Files.temporaryDirectory(Files.randomFilename());
        File file = Files.temporaryFile(parentTempDir);
        File zipFile = Files.temporaryFile();
        Zip.zip(parentTempDir, zipFile);
        HttpHandler urlHandler = new ClientHttpHandler();
        String jarUrl = String.format("jar:%s!/%s", zipFile.toURI(), Files.relativePath(parentTempDir, file));
        Response response = urlHandler.handle(get(jarUrl).build());
        assertThat(response.status(), is(Status.OK));
        assertThat(header(response, LAST_MODIFIED), is(Dates.RFC822().format(Dates.date(file.lastModified()))));
    }

    @Test
    public void canGetANonHttpUrl() throws Exception {
        URL resource = getClass().getResource("test.txt");
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(resource.toString()).build());
        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("This is a test file"));
    }

    @Test
    public void canGetAResource() throws Exception {
        Response response = handle(get("helloworld/queryparam?name=foo"), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello foo"));
    }

    @Test
    public void canPostToAResource() throws Exception {
        Response response = handle(post("helloworld/formparam").form("name", "foo"), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello foo"));
    }

    public static Response handle(final RequestBuilder request, final Server server) throws Exception {
        return handle(new ClientHttpHandler(0), request, server);
    }

    public static Response handle(int timeout, final RequestBuilder request, final Server server) throws Exception {
        return handle(new ClientHttpHandler(timeout), request, server);
    }

    public static Response handle(final HttpHandler client, final RequestBuilder request, final Server server) throws Exception {
        HttpHandler urlHandler = new AuditHandler(client, new PrintAuditor(Debug.debugging() ? System.out : Streams.nullPrintStream()));
        Uri uri = request.uri();
        Uri path = server.uri().mergePath(uri.path()).query(uri.query()).fragment(uri.fragment());
        return urlHandler.handle(request.uri(path).build());
    }

    private Server server;

    @Before
    public void setUp() throws Exception {
        server = application(HelloWorldApplication.class).start();
    }

    @After
    public void tearDown() throws Exception {
        server.close();
    }

}
