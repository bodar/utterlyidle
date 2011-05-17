package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Runnables;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.googlecode.yadic.Container;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.handlers.ClientHttpHandlerTest.handle;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

public abstract class ServerContract {
    protected Server server;

    protected abstract Server createServer(CloseableCallable<Application> application) throws Exception;

    @Before
    public void start() throws Exception {
        server = createServer(new RestApplicationActivator(new SingleResourceModule(HelloWorld.class)));
    }

    @After
    public void stop() throws Exception {
        server.close();
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
    public void stoppingTheServerClosesTheApplication() throws Exception {
        stop();
        ApplicationCloseableCallable application = new ApplicationCloseableCallable();
        server = createServer(application);
        stop();
        assertThat(application.closed(), is(true));
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
        Response response = handle(get("helloworld/headerparam").accepting("*/*").withHeader("name", "bar"), server);

        assertThat(new String(response.bytes()), is("Hello bar"));
    }

    @Test
    public void mapsResponseHeaders() throws Exception {
        Response response = handle(get("helloworld/inresponseheaders?name=mike").accepting("*/*"), server);

        assertThat(response.header("greeting"), is("Hello mike"));
    }

    @Test
    public void mapsStatusCode() throws Exception {
        Response response = handle(get("doesnotexist"), server);

        assertThat(response.status(), is(NOT_FOUND));
    }

    @Test
    public void canHandleMultiValueQueryParameters() throws Exception {
        Response response = handle(get("echoquery").withQuery("a", "first").withQuery("a", "second").accepting("*/*"), server);

        String result = new String(response.bytes());

        assertThat(result, containsString("first"));
        assertThat(result, containsString("second"));
    }

    @Test
    public void retainsOrderOfQueryParameters() throws Exception {
        Response response = handle(get("echoquery").withQuery("a", "1").withQuery("b", "2").withQuery("a", "3").withQuery("b", "4").accepting("*/*"), server);

        assertThat(new String(response.bytes()), is("?a=1&b=2&a=3&b=4"));
    }

    @Test
    public void willPrintStackTraceAsPlainText() throws Exception {
        ResponseAsString responseContent = new ResponseAsString();
        Pair<Integer, String> response = url(server.getUrl() + "goesbang?exceptionMessage=goes_bang").get(MediaType.WILDCARD, responseContent);

        assertThat(response.first(), is(Status.INTERNAL_SERVER_ERROR.code()));
        assertThat(responseContent.toString(), containsString("Exception"));
        assertThat(responseContent.toString(), containsString("goes_bang"));
    }


    public static class ResponseAsString implements Callable1<InputStream, Void> {

        private String value;

        public Void call(InputStream inputStream) {
            value = Strings.toString(inputStream);
            return Runnables.VOID;
        }

        public String toString() {
            return value;
        }

    }

    private class NullApplication implements Application {
        public Container applicationScope() {
            return null;
        }

        public <T> T usingRequestScope(Callable1<Container, T> callable) {
            return null;
        }

        public <T> T usingParameterScope(Request request, Callable1<Container, T> callable) {
            return null;
        }

        public Application add(Module module) {
            return null;
        }

        public void close() throws IOException {

        }

        public Response handle(Request request) throws Exception {
            return null;
        }
    }

    private class ApplicationCloseableCallable implements CloseableCallable<Application> {
        private boolean closed = false;

        public Application call() throws Exception {
            return new NullApplication();
        }

        public void close() throws IOException {
            closed = true;
        }

        public boolean closed() {
            return closed;
        }
    }
}
