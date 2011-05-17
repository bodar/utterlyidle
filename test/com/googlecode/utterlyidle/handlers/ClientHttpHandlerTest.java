package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.httpserver.RestServer;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientHttpHandlerTest {
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
        server = new RestServer(new RestApplication(new SingleResourceModule(HelloWorld.class)), ServerConfiguration.defaultConfiguration());
    }

    @After
    public void tearDown() throws Exception {
        server.close();
    }
}
