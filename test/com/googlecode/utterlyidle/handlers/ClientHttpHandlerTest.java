package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.URLs;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.httpserver.RestServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientHttpHandlerTest {
    @Test
    public void correctlyHandlesANotFoundFileUrl() throws Exception {
        URL resource = URLs.url("file:///bob");
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(resource.toString()).build());
        assertThat(response.status(), is(Status.NOT_FOUND));
    }

    @Test
    public void canGetANonHttpUrl() throws Exception {
        URL resource = getClass().getResource("test.txt");
        HttpHandler urlHandler = new ClientHttpHandler();
        Response response = urlHandler.handle(get(resource.toString()).build());
        assertThat(response.status(), is(Status.OK));
        assertThat(new String(response.bytes()), is("This is a test file"));
    }

    @Test
    public void canGetAResource() throws Exception {
        Response response = handle(get("helloworld/queryparam?name=foo"), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(new String(response.bytes()), is("Hello foo"));
    }

    @Test
    public void canPostToAResource() throws Exception {
        Response response = handle(post("helloworld/formparam").withForm("name", "foo"), server);
        assertThat(response.status(), is(Status.OK));
        assertThat(new String(response.bytes()), is("Hello foo"));
    }

    public static Response handle(final RequestBuilder request, final Server server) throws Exception {
        HttpHandler urlHandler = new ClientHttpHandler();
        return urlHandler.handle(request.withPath(server.getUrl().toString() + request.path()).build());
    }

    private Server server;

    @Before
    public void setUp() throws Exception {
        server = new RestServer(new HelloWorldApplication(), defaultConfiguration());
    }

    @After
    public void tearDown() throws Exception {
        server.close();
    }
}
