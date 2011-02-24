package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ServerContract;
import com.googlecode.utterlyidle.Status;
import com.googlecode.totallylazy.Strings;
import org.junit.AfterClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import java.net.HttpURLConnection;
import java.net.URL;

public class RestContainerTest extends ServerContract {
    private static RestServer server;

    protected void ensureServerIsStarted(Application application) throws Exception {
        if(server!=null)return;
        server = new RestServer(port(), application);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
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
