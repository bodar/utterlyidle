package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Debug;
import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Streams;
import com.googlecode.totallylazy.URLs;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.Zip;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientHttpHandlerTest {

    @Test
    public void correctlyHandlesChunkedTransferEncoding() throws Exception {
        Response response = handle(get(uri("chunk")), server);
        assertThat(response.headers().contains(HttpHeaders.TRANSFER_ENCODING), is(false));
        assertThat(response.headers().contains(HttpHeaders.CONTENT_LENGTH), is(true));
    }

    @Test
    public void correctlyHandlesTimeouts() throws Exception {
        Response response = handle(10, get("slow"), server);
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
    public void supportsLastModifiedOnFileUrls() throws Exception {
        File file = Files.temporaryFile();
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(file.toURI().toString()).build());
        assertThat(response.status(), is(Status.OK));
        assertThat(Response.methods.header(response, HttpHeaders.LAST_MODIFIED), is(Dates.RFC822().format(Dates.date(file.lastModified()))));
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
        assertThat(Response.methods.header(response, HttpHeaders.LAST_MODIFIED), is(Dates.RFC822().format(Dates.date(file.lastModified()))));
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
        return handle(0, request, server);
    }

    public static Response handle(int timeout, final RequestBuilder request, final Server server) throws Exception {
        HttpHandler urlHandler = new AuditHandler(new ClientHttpHandler(timeout), new PrintAuditor(Debug.debugging() ? System.out : Streams.nullPrintStream()));
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
