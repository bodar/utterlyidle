package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class RestContainerTest extends ServerContract {
    protected Server createServer(CloseableCallable<Application> appActivator) throws Exception {
        return new RestServer(port(), basePath("/"), appActivator);
    }

    protected int port() {
        return 8000;
    }

    @Test
    public void willOnlyHandleSingleValueHeadersBecauseSimpleWebDoesntSupportIt() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/echoheaders").openConnection();
        urlConnection.setRequestProperty("accept", "*/*");
        urlConnection.setRequestProperty("someheader", "first value");
        urlConnection.setRequestProperty("someheader", "second value");

        String result = Strings.toString(urlConnection.getInputStream());

        assertThat(result, not(containsString("first value")));
        assertThat(result, containsString("second value"));
    }

    @Test
    public void willNotHandleMultipleAcceptHeaders() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + port() + "/html").openConnection();
        urlConnection.setRequestProperty("accept", "text/plain");
        urlConnection.setRequestProperty("accept", "text/html");
        urlConnection.setRequestProperty("accept", "text/xml");

        assertThat(urlConnection.getResponseCode(), is(Status.NOT_ACCEPTABLE.code()));
    }
}
