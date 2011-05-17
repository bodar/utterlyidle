package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.httpserver.RestServer;
import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
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
        server = new RestServer(new RestApplicationActivator(new SingleResourceModule(HelloWorld.class)));

    }
}
