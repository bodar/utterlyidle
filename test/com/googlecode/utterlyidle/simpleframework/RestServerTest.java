package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.io.Url;
import org.junit.Test;

import java.net.HttpURLConnection;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class RestServerTest extends ServerContract {
    protected Server createServer(CloseableCallable<Application> appActivator) throws Exception {
        return new RestServer(basePath("/"), appActivator);
    }

    @Test
    public void willOnlyHandleSingleValueHeadersBecauseSimpleWebDoesntSupportIt() throws Exception {
        Url url = server.getUrl();
        HttpURLConnection urlConnection = (HttpURLConnection) urlOf("echoheaders").openConnection();
        urlConnection.setRequestProperty("accept", "*/*");
        urlConnection.setRequestProperty("someheader", "first value");
        urlConnection.setRequestProperty("someheader", "second value");

        String result = Strings.toString(urlConnection.getInputStream());

        assertThat(result, not(containsString("first value")));
        assertThat(result, containsString("second value"));
    }

    @Test
    public void willNotHandleMultipleAcceptHeaders() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) urlOf("html").openConnection();
        urlConnection.setRequestProperty("accept", "text/plain");
        urlConnection.setRequestProperty("accept", "text/html");
        urlConnection.setRequestProperty("accept", "text/xml");

        assertThat(urlConnection.getResponseCode(), is(Status.NOT_ACCEPTABLE.code()));
    }
}
