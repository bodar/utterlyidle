package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.Base64;
import com.googlecode.utterlyidle.BaseUri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.HttpHeaders.WWW_AUTHENTICATE;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.UNAUTHORIZED;
import static com.googlecode.utterlyidle.authentication.Credential.credential;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicHttpHandlerTest {
    @Test
    public void returnsUnauthorizedWhenNoCredentials() throws Exception {
        Response response = basicServer(throwingHandler()).handle(get("/").build());
        assertThat(response.status(), is(UNAUTHORIZED));
        assertThat(response.headers().getValue(WWW_AUTHENTICATE), is("Basic realm=example.com"));
    }

    @Test
    public void returnsUnauthorizedWhenCredentialsDontMatch() throws Exception {
        Response response = basicServer(throwingHandler()).
                handle(get("/").header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode(bytes("dan:wrong"))).build());
        assertThat(response.status(), is(UNAUTHORIZED));
        assertThat(response.headers().getValue(WWW_AUTHENTICATE), is("Basic realm=example.com"));
    }

    @Test
    public void letsRequestThroughWhenCredentialsMatch() throws Exception {
        Response response = basicServer(returns(response(OK))).
                handle(get("/").header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode(bytes("dan:right"))).build());
        assertThat(response.status(), is(OK));
    }

    public static HttpHandler basicServer(final HttpHandler handler) {
        BaseUri baseUri = BaseUri.baseUri("http://example.com/");
        Credentials credentials = Credentials.credentials(credential("dan", "right"));
        return new BasicHttpHandler(handler, baseUri, credentials);
    }

    public static HttpHandler throwingHandler() {
        return new HttpHandler() {
            @Override
            public Response handle(final Request request) throws Exception {
                throw new UnsupportedOperationException();
            }
        };
    }

}