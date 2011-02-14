package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Runnable1;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public abstract class ServerContract {
    protected abstract void ensureServerIsStarted(Application application) throws Exception;

    protected abstract int port();

    @Before
    public void startServer() throws Exception {
        ensureServerIsStarted(new RestApplication().add(new SingleResourceModule(HelloWorld.class)));
    }

    @Test
    public void handlesGets() throws Exception {
        InputAsString output = new InputAsString();
        Pair<Integer, String> status = Url.url("http://localhost:" + port() + "/helloworld/queryparam?name=foo").get("*/*", output);

        assertThat(status.first(), is(200));
        assertThat(output.value(), is("Hello foo"));
    }

    @Test
    public void handlesPosts() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/helloworld/formparam").openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("accept", "*/*");
        urlConnection.getOutputStream().write("name=fred".getBytes());

        String result = Strings.toString(urlConnection.getInputStream());

        assertThat(result, is("Hello fred"));
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
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/echoquery?param=firstvalue&param=secondvalue").openConnection();

        String result = Strings.toString(urlConnection.getInputStream());

        assertThat(result, containsString("firstvalue"));
        assertThat(result, containsString("secondvalue"));
    }

    private static class InputAsString implements Runnable1<InputStream> {
        private String value;

        public void run(InputStream inputStream) {
            value = Strings.toString(inputStream);
        }

        public String value() {
            return value;
        }
    }
}
