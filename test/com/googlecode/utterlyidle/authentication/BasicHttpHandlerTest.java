package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Sets;
import com.googlecode.totallylazy.security.Base64;
import com.googlecode.utterlyidle.*;
import org.junit.Test;

import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.WWW_AUTHENTICATE;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.UNAUTHORIZED;
import static com.googlecode.utterlyidle.authentication.Credentials.credential;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicHttpHandlerTest {
    @Test
    public void returnsUnauthorizedWhenNoCredentials() throws Exception {
        Response response = basicServer(throwing()).handle(Request.get("/"));
        assertThat(response.status(), is(UNAUTHORIZED));
        assertThat(response.headers().getValue(WWW_AUTHENTICATE), is("Basic realm=\"example.com\""));
    }

    @Test
    public void returnsUnauthorizedWhenCredentialsDontMatch() throws Exception {
        Response response = basicServer(throwing()).
                handle(Request.get("/", HttpMessage.Builder.header(AUTHORIZATION, "Basic " + Base64.encode(bytes("dan:wrong")))));
        assertThat(response.status(), is(UNAUTHORIZED));
        assertThat(response.headers().getValue(WWW_AUTHENTICATE), is("Basic realm=\"example.com\""));
    }

    @Test
    public void letsRequestThroughWhenCredentialsMatch() throws Exception {
        Response response = basicServer(returns(Response.response(OK))).
                handle(Request.get("/", HttpMessage.Builder.header(AUTHORIZATION, "Basic " + Base64.encode(bytes("dan:right")))));
        assertThat(response.status(), is(OK));
    }

    public static HttpHandler basicServer(final HttpHandler handler) {
        BaseUri baseUri = BaseUri.baseUri("http://example.com/");
        return new BasicHttpHandler(handler, baseUri, Sets.set(credential("dan", "right")));
    }

    public static HttpHandler throwing() {
        return request -> {
            throw new UnsupportedOperationException();
        };
    }

}