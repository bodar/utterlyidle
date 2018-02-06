package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Debug;
import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Streams;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.io.URLs;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.io.Zip;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.ClientConfiguration;
import com.googlecode.utterlyidle.Entity;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Protocol;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.RestTest;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.ssl.SecureString;
import com.googlecode.utterlyidle.ssl.SecureStringTest;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import static com.googlecode.totallylazy.Assert.assertTrue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Streams.emptyInputStream;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.totallylazy.matchers.NumberMatcher.greaterThan;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.ClientConfiguration.Builder.clientConfiguration;
import static com.googlecode.utterlyidle.Entities.inputStreamOf;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.LAST_MODIFIED;
import static com.googlecode.utterlyidle.HttpMessage.Builder.entity;
import static com.googlecode.utterlyidle.Request.Builder.form;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.Request.patch;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.handlers.RequestTimeout.requestTimeout;
import static com.googlecode.utterlyidle.ssl.SSL.keyStore;
import static com.googlecode.utterlyidle.ssl.SSL.sslContext;
import static com.googlecode.utterlyidle.ssl.SecureString.secureString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class ClientHttpHandlerTest {
    @Test(expected = UnsupportedOperationException.class)
    public void doNotAllowHttpURLConnectionToConvertGetRequestWithEntityToPost() throws Exception {
        Request request = Request.request(GET, uri("helloworld/queryparam?name=foo"), headerParameters(), Entity.entity(emptyInputStream()));
        handle(request, server);
    }

    @Test(expected = MalformedURLException.class)
    public void doesNotThrowNullPointerExceptionWhenNoSchema() throws Exception {
        new ClientHttpHandler().handle(get("relative/uri"));
    }

    @Test
    public void supportsPatch() throws Exception {
        Server server = application().addAnnotated(RestTest.PatchContent.class).start();
        Response response = new ClientHttpHandler().handle(patch(server.uri().mergePath("path/bar")).entity("input"));

        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("input"));
        server.close();
    }

    @Test
    public void supportsPatchOverHttps() throws Exception {
        try (InputStream resource = SecureStringTest.class.getResourceAsStream("localhost.jks");
             SecureString password = secureString('p', 'a', 's', 's', 'w', 'o', 'r', 'd')) {
            SSLContext context = sslContext(keyStore(password, resource), password);
            Server server = application().addAnnotated(RestTest.PatchContent.class).start(defaultConfiguration().sslContext(context));
            ClientHttpHandler client = new ClientHttpHandler(clientConfiguration(ClientConfiguration.Builder.sslContext(context)));
            Response response = client.handle(patch(server.uri().mergePath("path/bar")).entity("input"));

            assertThat(response.status(), is(Status.OK));
            assertThat(response.entity().toString(), is("input"));
            server.close();
        }
    }

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
        final Response response = handle(Request.put("echo", entity(inputStreamOf("Hello"))), server);
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
        Response response = handle(Request.put("echo", entity("Hello")), server);
        assertThat(response.entity().toString(), Matchers.is("Hello"));
    }

    @Test
    public void canPutByteArray() throws Exception {
        Response response = handle(Request.put("echo", entity(bytes("Hello"))), server);
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
        Response response = new ClientHttpHandler().handle(get(uri("http://127.0.0.1:0/")));
        assertThat(response.status(), is(Status.CONNECTION_REFUSED));
    }

    @Test
    public void correctlyHandlesANotFoundFileUrl() throws Exception {
        URL resource = URLs.url("file:///bob");
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(resource.toString()));
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
        Response response = client.handle(Request.put(uri, HttpMessage.Builder.header(LAST_MODIFIED, lastModified), entity(content)));
        assertThat(response.status(), is(Status.CREATED));
        assertThat(response.header(HttpHeaders.LOCATION).get(), is(uri.toString()));
        assertThat(file.exists(), is(true));
        assertThat(file.lastModified(), is(lastModified.getTime()));
        assertThat(Strings.toString(file), is(content));
    }

    @Test
    public void supportsLastModifiedOnFileUrls() throws Exception {
        File file = Files.temporaryFile();
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(uri(file)));
        assertThat(response.status(), is(Status.OK));
        assertThat(response.header(LAST_MODIFIED).get(), is(Dates.RFC822().format(Dates.date(file.lastModified()))));
    }

    @Test
    public void supportsLastModifiedOnJarUrls() throws Exception {
        File parentTempDir = Files.temporaryDirectory(Files.randomFilename());
        File file = Files.temporaryFile(parentTempDir);
        File zipFile = Files.temporaryFile();
        Zip.zip(parentTempDir, zipFile);
        HttpHandler urlHandler = new ClientHttpHandler();
        String jarUrl = String.format("jar:%s!/%s", zipFile.toURI(), Files.relativePath(parentTempDir, file));
        Response response = urlHandler.handle(get(jarUrl));
        assertThat(response.status(), is(Status.OK));
        assertThat(response.header(LAST_MODIFIED).get(), is(Dates.RFC822().format(Dates.date(file.lastModified()))));
    }

    @Test
    public void canGetANonHttpUrl() throws Exception {
        URL resource = getClass().getResource("test.txt");
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(resource.toString()));
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
        Response response = handle(Request.post("helloworld/formparam", form("name", "foo")), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(response.entity().toString(), is("Hello foo"));
    }

    public static Response handle(final Request request, final Server server) throws Exception {
        return handle(0, request, server);
    }

    public static Response handle(int timeout, final Request request, final Server server) throws Exception {
        return handle(new ClientHttpHandler(timeout), request, server);
    }

    public static Response handle(final HttpHandler client, final Request request, final Server server) throws Exception {
        HttpHandler urlHandler = new AuditHandler(client, new PrintAuditor(Debug.debugging() ? System.out : Streams.nullPrintStream()));
        Uri uri = request.uri();
        Uri path = server.uri().mergePath(uri.path()).query(uri.query()).fragment(uri.fragment());
        return urlHandler.handle(modify(request, Request.Builder.uri(path)));
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
