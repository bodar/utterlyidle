package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Runnables;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.io.Url;

import static com.googlecode.totallylazy.Runnables.write;
import static com.googlecode.utterlyidle.io.Url.url;

import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.SingleResourceModule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;

import com.googlecode.yadic.Container;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

public abstract class ServerContract {
    protected Server server;
    protected abstract Server createServer(CloseableCallable<Application> application) throws Exception;

    protected abstract int port();

    @Before
    public void start() throws Exception {
        server = createServer(new RestApplicationActivator(new SingleResourceModule(HelloWorld.class)));
    }

    @After
    public void stop() throws Exception {
        server.close();
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
        ResponseAsString output = new ResponseAsString();
        Pair<Integer, String> status = Url.url("http://localhost:" + port() + "/helloworld/queryparam?name=foo").get("*/*", output);

        assertThat(status.first(), is(200));
        assertThat(output.toString(), is("Hello foo"));
    }

    @Test
    public void handlesPosts() throws Exception {
        ResponseAsString output = new ResponseAsString();
        Pair<Integer, String> status = Url.url("http://localhost:" + port() + "/helloworld/formparam").post(MediaType.APPLICATION_FORM_URLENCODED, write("name=fred".getBytes()), output);

        assertThat(status.first(), is(200));
        assertThat(output.toString(), is("Hello fred"));
    }

    @Test
    public void mapsRequestHeaders() throws Exception {
        URLConnection urlConnection = new URL("http://localhost:" + port() + "/helloworld/headerparam").openConnection();
        urlConnection.setRequestProperty("accept", "*/*");
        urlConnection.setRequestProperty("name", "bar");

        String result = Strings.toString(urlConnection.getInputStream());

        assertThat(result, is("Hello bar"));
    }

    @Test
    public void mapsResponseHeaders() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/helloworld/inresponseheaders?name=mike").openConnection();
        urlConnection.setRequestProperty("accept", "*/*");

        String result = urlConnection.getHeaderField("greeting");

        assertThat(result, is("Hello mike"));
    }

    @Test
    public void mapsStatusCode() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/doesnotexist").openConnection();

        assertThat(urlConnection.getResponseCode(), is(404));
    }

    @Test
    public void canHandleMultiValueQueryParameters() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/echoquery?a=first&a=second").openConnection();
        urlConnection.setRequestProperty("accept", "*/*");

        String result = Strings.toString(urlConnection.getInputStream());

        assertThat(result, containsString("first"));
        assertThat(result, containsString("second"));
    }

    @Test
    public void retainsOrderOfQueryParameters() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/echoquery?a=1&b=2&a=3&b=4").openConnection();
        urlConnection.setRequestProperty("accept", "*/*");

        String result = Strings.toString(urlConnection.getInputStream());

        assertThat(result, is("?a=1&b=2&a=3&b=4"));
    }

    @Test
    public void willPrintStackTraceAsPlainText() throws Exception {
        ResponseAsString responseContent = new ResponseAsString();
        Pair<Integer, String> response = url("http://localhost:" + port() + "/goesbang?exceptionMessage=goes_bang").get(MediaType.WILDCARD, responseContent);

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

        public <T> T usingArgumentScope(Request request, Callable1<Container, T> callable) {
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
