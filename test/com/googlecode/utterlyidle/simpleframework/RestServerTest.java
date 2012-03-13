package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.Entity;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerContract;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Status.NOT_ACCEPTABLE;
import static com.googlecode.utterlyidle.handlers.ClientHttpHandlerTest.handle;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class RestServerTest extends ServerContract {
    @Override
    protected Class<? extends Server> server() throws Exception {
        return RestServer.class;
    }

    @Test
    public void willOnlyHandleSingleValueHeadersBecauseSimpleWebDoesntSupportIt() throws Exception {
        Response response = handle(get("echoheaders").accepting("*/*").withHeader("someheader", "first value").withHeader("someheader", "second value"), server);
        String result = Entity.asString(response);

        assertThat(result, not(containsString("first value")));
        assertThat(result, containsString("second value"));
    }

    @Test
    public void willNotHandleMultipleAcceptHeaders() throws Exception {
        Response response = handle(get("html").accepting("text/plain").accepting("text/html").accepting("text/xml"), server);
        assertThat(response.status(), is(NOT_ACCEPTABLE));
    }
}
